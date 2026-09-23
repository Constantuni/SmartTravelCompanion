package com.example.smarttravelcompanion.data

import com.google.gson.annotations.SerializedName

data class PlacesResponse(
    val results: List<PlaceResult>
)

data class PlaceResult(
    val name: String,
    val vicinity: String,
    val rating: Double? = 0.0,
    val photos: List<Photo>? = null
)

data class Photo(
    @SerializedName("photo_reference")
    val photoReference: String
)