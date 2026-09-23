package com.example.smarttravelcompanion.utils

object FirebaseHelper {
    private const val DB_URL = "https://smarttravelcompanionapp-default-rtdb.europe-west1.firebasedatabase.app/"
    private val database = com.google.firebase.database.FirebaseDatabase.getInstance(DB_URL).getReference("favorites")

    fun toggleFavorite(placeName: String, address: String, currentlyFavorited: Boolean) {
        val safeKey = placeName.replace(".", "").replace("#", "").replace("$", "").replace("[", "").replace("]", "")

        if (currentlyFavorited) {
            database.child(safeKey).removeValue()
                .addOnSuccessListener { android.util.Log.d("FIREBASE", "Successfully Removed: $placeName") }
                .addOnFailureListener { android.util.Log.e("FIREBASE", "Delete Failed: ${it.message}") }
        } else {
            val favorite = com.example.smarttravelcompanion.data.FavoritePlace(
                name = placeName,
                address = address,
                timestamp = System.currentTimeMillis()
            )
            database.child(safeKey).setValue(favorite)
                .addOnSuccessListener { android.util.Log.d("FIREBASE", "Successfully Added: $placeName") }
        }
    }
}