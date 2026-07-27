package com.austinlocal.model;

import jakarta.persistence.*;

/**
 * Tracks how much a given user likes a given category, based on past
 * interactions (likes/visits). Score is incremented each time a user
 * "likes" a place in that category. Used by RecommendationService to
 * re-rank search results.
 */
@Entity
@Table(name = "user_preferences", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "category"})
})
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String category;

    private double affinityScore; // grows as the user likes places in this category

    public UserPreference() {}

    public UserPreference(String userId, String category, double affinityScore) {
        this.userId = userId;
        this.category = category;
        this.affinityScore = affinityScore;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getAffinityScore() { return affinityScore; }
    public void setAffinityScore(double affinityScore) { this.affinityScore = affinityScore; }
}
