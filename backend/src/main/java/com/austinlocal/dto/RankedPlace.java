package com.austinlocal.dto;

import com.austinlocal.model.Place;

public class RankedPlace {

    private Long id;
    private String name;
    private String category;
    private String description;
    private double latitude;
    private double longitude;
    private double rating;
    private boolean currentlyOpen;
    private double distanceKm;
    private double score; // final ranking score combining distance, rating, and personalization

    public RankedPlace(Place place, double distanceKm, double score) {
        this.id = place.getId();
        this.name = place.getName();
        this.category = place.getCategory();
        this.description = place.getDescription();
        this.latitude = place.getLatitude();
        this.longitude = place.getLongitude();
        this.rating = place.getRating();
        this.currentlyOpen = place.isCurrentlyOpen();
        this.distanceKm = distanceKm;
        this.score = score;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getRating() { return rating; }
    public boolean isCurrentlyOpen() { return currentlyOpen; }
    public double getDistanceKm() { return distanceKm; }
    public double getScore() { return score; }
}
