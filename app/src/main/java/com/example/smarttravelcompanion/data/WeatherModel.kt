package com.example.smarttravelcompanion.data

data class WeatherResponse(
    val coord: Coordinates,
    val main: MainData,
    val weather: List<WeatherDescription>,
    val name: String
)

data class Coordinates(
    val lat: Double,
    val lon: Double
)

data class MainData(
    val temp: Double,
    val humidity: Int,
    val pressure: Int
)

data class WeatherDescription(
    val main: String,
    val description: String,
    val icon: String
)