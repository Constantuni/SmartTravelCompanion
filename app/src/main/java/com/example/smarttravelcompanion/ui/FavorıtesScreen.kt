package com.example.smarttravelcompanion.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.smarttravelcompanion.data.FavoritePlace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(viewModel: WeatherViewModel, onBack: () -> Unit) {
    val favoritesList = viewModel.favoritePlaces

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Favorites", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            items(items = favoritesList) { place: FavoritePlace ->
                FavoriteItem(place)
            }
        }
    }
}

@Composable
fun FavoriteItem(place: FavoritePlace) {
    ListItem(
        headlineContent = { Text(place.name ?: "Unknown", fontWeight = FontWeight.Bold) },
        supportingContent = { Text(place.address ?: "No address") },
        leadingContent = { Text("📍") }
    )
    HorizontalDivider()
}