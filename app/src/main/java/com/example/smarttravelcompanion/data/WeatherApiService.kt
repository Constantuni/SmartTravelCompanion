package com.example.smarttravelcompanion.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

private const val WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/"

interface WeatherApiService {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    @GET
    suspend fun getNearbyPlaces(
        @Url url: String,
        @Query("location") location: String,
        @Query("radius") radius: Int = 5000,
        @Query("type") type: String = "tourist_attraction",
        @Query("key") apiKey: String
    ): PlacesResponse
}

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(WEATHER_BASE_URL)
        .build()

    val service: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}