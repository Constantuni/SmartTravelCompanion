package com.example.smarttravelcompanion.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttravelcompanion.data.FavoritePlace
import com.example.smarttravelcompanion.data.RetrofitClient
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    var weatherUiState: WeatherUiState by mutableStateOf(WeatherUiState.Loading)
        private set
    var placesUiState: PlacesUiState by mutableStateOf(PlacesUiState.Loading)
        private set

    var favoritePlaceIds by mutableStateOf(setOf<String>())
        private set


    var favoritePlaces by mutableStateOf<List<FavoritePlace>>(emptyList())
        private set

    private val weatherApiKey = "WEATHER_API_KEY_removed"
    private val googleApiKey = "MAPS_PLATFORM_API_KEY_removed"

    private var currentLat: Double = 0.0
    private var currentLon: Double = 0.0

    init {
        fetchFavoritesFromFirebase()
    }

    fun getTravelData(lat: Double, lon: Double) {
        currentLat = lat
        currentLon = lon
        getWeather(lat, lon)
        getNearbyPlaces(lat, lon)
    }

    private fun fetchFavoritesFromFirebase() {
        val dbUrl = "https://smarttravelcompanionapp-default-rtdb.europe-west1.firebasedatabase.app/"
        val database = com.google.firebase.database.FirebaseDatabase.getInstance(dbUrl).getReference("favorites")

        database.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val ids = mutableSetOf<String>()
                val fullPlaces = mutableListOf<FavoritePlace>()

                for (child in snapshot.children) {
                    val name = child.child("name").getValue(String::class.java)
                    name?.let { ids.add(it) }

                    val place = child.getValue(FavoritePlace::class.java)
                    place?.let { fullPlaces.add(it) }
                }
                favoritePlaceIds = ids
                favoritePlaces = fullPlaces
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                android.util.Log.e("FIREBASE_ERROR", error.message)
            }
        })
    }

    private fun getWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            weatherUiState = WeatherUiState.Loading
            try {
                val result = RetrofitClient.service.getWeather(lat, lon, weatherApiKey)
                weatherUiState = WeatherUiState.Success(result)

                // Requirement #5: Save search history to Firebase
                val historyRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("history")
                historyRef.push().setValue(result.name)
            } catch (e: Exception) {
                weatherUiState = WeatherUiState.Error
            }
        }
    }

    fun searchByCategory(lat: Double, lon: Double, category: String) {
        viewModelScope.launch {
            placesUiState = PlacesUiState.Loading
            try {
                val result = RetrofitClient.service.getNearbyPlaces(
                    url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json",
                    location = "$lat,$lon",
                    type = category.lowercase(),
                    apiKey = googleApiKey
                )
                placesUiState = PlacesUiState.Success(result.results)
            } catch (e: Exception) {
                placesUiState = PlacesUiState.Error
            }
        }
    }

    fun getNearbyPlaces(lat: Double, lon: Double) {
        viewModelScope.launch {
            placesUiState = PlacesUiState.Loading
            try {
                val result = RetrofitClient.service.getNearbyPlaces(
                    url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json",
                    location = "$lat,$lon",
                    apiKey = googleApiKey
                )
                android.util.Log.d("DEBUG_PLACES", "Found ${result.results.size} places")
                placesUiState = PlacesUiState.Success(result.results)
            } catch (e: Exception) {
                android.util.Log.e("DEBUG_PLACES", "Error: ${e.message}")
                placesUiState = PlacesUiState.Error
            }
        }
    }
}