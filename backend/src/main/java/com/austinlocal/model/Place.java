package com.austinlocal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "places")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category; // e.g. "coffee", "live_music", "food_truck", "park", "bar"

    private String description;

    private double latitude;

    private double longitude;

    private double rating; // 0.0 - 5.0

    private boolean currentlyOpen;

    public Place() {}

    public Place(String name, String category, String description,
                 double latitude, double longitude, double rating, boolean currentlyOpen) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.currentlyOpen = currentlyOpen;
    }

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public boolean isCurrentlyOpen() { return currentlyOpen; }
    public void setCurrentlyOpen(boolean currentlyOpen) { this.currentlyOpen = currentlyOpen; }
}
