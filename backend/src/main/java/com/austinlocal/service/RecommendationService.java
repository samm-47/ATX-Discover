package com.austinlocal.service;

import com.austinlocal.model.UserPreference;
import com.austinlocal.repository.UserPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Rules-based personalization layer. Not a full ML recommender —
 * a transparent, tunable scoring function is easier to reason about
 * and debug for a v1 product, and is a common real-world starting
 * point before investing in a learned model.
 *
 * Final score blends three signals:
 *   - proximity   (closer is better)
 *   - quality     (higher-rated places rank higher)
 *   - affinity    (categories the user has liked before rank higher)
 */
@Service
public class RecommendationService {

    private final UserPreferenceRepository preferenceRepository;

    // Tunable weights. In a real system these would be tuned against
    // click-through / conversion data rather than hand-picked.
    private static final double DISTANCE_WEIGHT = 1.0;
    private static final double RATING_WEIGHT = 0.6;
    private static final double AFFINITY_WEIGHT = 0.8;

    @Autowired
    public RecommendationService(UserPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    /**
     * Computes a ranking score for a place given its distance from the user
     * and the user's historical affinity for its category. Higher is better.
     */
    public double score(String userId, String category, double rating, double distanceKm) {
        double proximityScore = 1.0 / (1.0 + distanceKm); // decays smoothly with distance
        double affinity = getAffinity(userId, category);

        return (DISTANCE_WEIGHT * proximityScore)
                + (RATING_WEIGHT * (rating / 5.0))
                + (AFFINITY_WEIGHT * affinity);
    }

    private double getAffinity(String userId, String category) {
        if (userId == null) return 0.0;
        return preferenceRepository.findByUserIdAndCategory(userId, category)
                .map(UserPreference::getAffinityScore)
                .orElse(0.0);
    }

    /**
     * Records a "like" from a user for a category, incrementing their
     * affinity score. Called when a user favorites/likes a place.
     */
    public void recordLike(String userId, String category) {
        UserPreference pref = preferenceRepository.findByUserIdAndCategory(userId, category)
                .orElse(new UserPreference(userId, category, 0.0));

        // Simple incremental scheme, capped so one category can't fully dominate.
        double updated = Math.min(pref.getAffinityScore() + 0.15, 1.0);
        pref.setAffinityScore(updated);

        preferenceRepository.save(pref);
    }
}
