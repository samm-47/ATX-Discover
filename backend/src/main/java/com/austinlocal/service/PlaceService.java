package com.austinlocal.service;

import com.austinlocal.config.CacheConfig;
import com.austinlocal.dto.RankedPlace;
import com.austinlocal.model.Place;
import com.austinlocal.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final GeoService geoService;
    private final RecommendationService recommendationService;

    @Autowired
    public PlaceService(PlaceRepository placeRepository,
                         GeoService geoService,
                         RecommendationService recommendationService) {
        this.placeRepository = placeRepository;
        this.geoService = geoService;
        this.recommendationService = recommendationService;
    }

    /**
     * Finds places within radiusKm of the given point, ranked by a blend of
     * distance, rating, and (if userId is provided) personal category affinity.
     *
     * Cached per (lat, lon, radius, category, userId) so repeated hits on
     * popular areas/times — e.g. "downtown Austin, tonight" — don't recompute
     * distance + ranking over the full place set on every request.
     */
    @Cacheable(
            value = CacheConfig.NEARBY_PLACES_CACHE,
            key = "#latitude + ':' + #longitude + ':' + #radiusKm + ':' + #category + ':' + #userId"
    )
    public List<RankedPlace> findNearby(double latitude, double longitude, double radiusKm,
                                         String category, String userId) {

        List<Place> candidates = (category == null || category.isBlank())
                ? placeRepository.findAll()
                : placeRepository.findByCategory(category);

        return candidates.stream()
                .map(place -> {
                    double distance = geoService.distanceKm(latitude, longitude,
                            place.getLatitude(), place.getLongitude());
                    return new Object[]{place, distance};
                })
                .filter(pair -> (double) pair[1] <= radiusKm)
                .map(pair -> {
                    Place place = (Place) pair[0];
                    double distance = (double) pair[1];
                    double score = recommendationService.score(
                            userId, place.getCategory(), place.getRating(), distance);
                    return new RankedPlace(place, distance, score);
                })
                .sorted(Comparator.comparingDouble(RankedPlace::getScore).reversed())
                .collect(Collectors.toList());
    }

    public Place getById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Place not found: " + id));
    }
}
