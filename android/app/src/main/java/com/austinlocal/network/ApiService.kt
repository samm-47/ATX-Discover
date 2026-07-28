package com.austinlocal.network

import com.austinlocal.model.Place
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Body

interface ApiService {

    @GET("api/places/nearby")
    suspend fun getNearbyPlaces(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("radiusKm") radiusKm: Double = 5.0,
        @Query("category") category: String? = null,
        @Query("userId") userId: String? = null
    ): List<Place>

    @GET("api/places/{id}")
    suspend fun getPlace(@Path("id") id: Long): Place

    @POST("api/places/{id}/like")
    suspend fun likePlace(@Path("id") id: Long, @Body body: Map<String, String>)
}
