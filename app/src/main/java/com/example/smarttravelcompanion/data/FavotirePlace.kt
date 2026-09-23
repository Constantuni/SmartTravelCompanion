package com.example.smarttravelcompanion.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class FavoritePlace(
    val name: String? = null,
    val address: String? = null,
    val timestamp: Long? = null
)