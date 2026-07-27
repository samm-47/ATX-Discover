package com.austinlocal.model

data class Place(
    val id: Long,
    val name: String,
    val category: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double,
    val currentlyOpen: Boolean,
    val distanceKm: Double,
    val score: Double
)
