package com.austinlocal.viewmodel

import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austinlocal.model.Place
import com.austinlocal.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class SearchViewModel : ViewModel() {

    var places by mutableStateOf<List<Place>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var currentLocationName by mutableStateOf("Downtown Austin")
        private set

    var lat by mutableStateOf(30.2672)
        private set

    var lon by mutableStateOf(-97.7431)
        private set

    private var currentCategory: String? = null

    fun search(category: String? = currentCategory, userId: String? = "sam") {
        currentCategory = category
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                places = RetrofitClient.apiService.getNearbyPlaces(
                    lat = lat,
                    lon = lon,
                    radiusKm = 15.0,
                    category = category,
                    userId = userId
                )
            } catch (e: Exception) {
                errorMessage = "Couldn't load places: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun resetLocation() {
        lat = 30.2672
        lon = -97.7431
        currentLocationName = "Downtown Austin"
        search()
    }

    fun updateLocation(query: String, context: Context) {
        viewModelScope.launch {
            isLoading = true
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = withContext(Dispatchers.IO) {
                    geocoder.getFromLocationName(query, 1)
                }

                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    lat = address.latitude
                    lon = address.longitude
                    currentLocationName = address.locality ?: address.featureName ?: query
                    search()
                } else {
                    errorMessage = "Location not found: $query"
                }
            } catch (e: Exception) {
                errorMessage = "Error finding location: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun likePlace(id: Long, userId: String = "sam") {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.likePlace(id, mapOf("userId" to userId))
                search(userId = userId)
            } catch (e: Exception) {
                errorMessage = "Couldn't save like: ${e.message}"
            }
        }
    }
}
