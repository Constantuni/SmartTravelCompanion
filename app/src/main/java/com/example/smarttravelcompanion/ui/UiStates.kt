package com.example.smarttravelcompanion.ui

import com.example.smarttravelcompanion.data.PlaceResult
import com.example.smarttravelcompanion.data.WeatherResponse

sealed interface WeatherUiState {
    data class Success(val weather: WeatherResponse) : WeatherUiState
    object Error : WeatherUiState
    object Loading : WeatherUiState
}

sealed interface PlacesUiState {
    data class Success(val places: List<PlaceResult>) : PlacesUiState
    object Error : PlacesUiState
    object Loading : PlacesUiState
}