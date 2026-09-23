package com.example.smarttravelcompanion.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smarttravelcompanion.data.PlaceResult
import com.example.smarttravelcompanion.data.WeatherResponse
import com.example.smarttravelcompanion.utils.FirebaseHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(weatherViewModel: WeatherViewModel, onGoToFavorites: () -> Unit) {
    val weatherState = weatherViewModel.weatherUiState
    val placesState = weatherViewModel.placesUiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Travel", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onGoToFavorites) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorites", tint = Color.Red)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                when (weatherState) {
                    is WeatherUiState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                    is WeatherUiState.Success -> WeatherHeader(weatherState.weather)
                    is WeatherUiState.Error -> Text("Error loading weather.", color = Color.Red)
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                if (weatherState is WeatherUiState.Success) {
                    val lat = weatherState.weather.coord.lat
                    val lon = weatherState.weather.coord.lon

                    CategorySection(onCategorySelected = { selectedCategory ->
                        weatherViewModel.searchByCategory(lat, lon, selectedCategory)
                    })
                }
            }

            item {
                Text(
                    text = "Nearby Attractions",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }

            when (placesState) {
                is PlacesUiState.Loading -> item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) }
                is PlacesUiState.Success -> {
                    items(placesState.places) { place ->
                        AttractionItem(place = place, viewModel = weatherViewModel)
                    }
                }
                is PlacesUiState.Error -> item { Text("Could not load attractions.") }
            }
        }
    }
}

@Composable
fun WeatherHeader(weather: WeatherResponse) {
    val iconCode = weather.weather[0].icon
    val iconUrl = "https://openweathermap.org/img/wn/$iconCode@4x.png"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = weather.name, fontSize = 20.sp, fontWeight = FontWeight.Medium)
            AsyncImage(
                model = iconUrl,
                contentDescription = weather.weather[0].description,
                modifier = Modifier.size(100.dp)
            )
            Text(text = "${weather.main.temp.toInt()}°C", fontSize = 56.sp, fontWeight = FontWeight.Bold)
            Text(
                text = weather.weather[0].description.replaceFirstChar { it.uppercase() },
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun CategorySection(onCategorySelected: (String) -> Unit) {
    val categories = listOf("Restaurant", "Park", "Museum", "Cafe", "Zoo")

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Category",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = false,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category) },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

@Composable
fun AttractionItem(place: PlaceResult, viewModel: WeatherViewModel) {
    val context = LocalContext.current
    val isFavorited = viewModel.favoritePlaceIds.contains(place.name)
    val photoRef = place.photos?.firstOrNull()?.photoReference
    val imageUrl = getPlacePhotoUrl(photoRef, "GOOGLE_API_KEY_removed")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                // Requirement #4: Open Map Navigation
                val gmmIntentUri = Uri.parse("google.navigation:q=${place.vicinity}&mode=d")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                context.startActivity(mapIntent)
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(text = place.name, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(text = "📍 ${place.vicinity}", fontSize = 12.sp, color = Color.Gray, maxLines = 2)
            }

            IconButton(onClick = {
                FirebaseHelper.toggleFavorite(place.name, place.vicinity, isFavorited)
            }) {
                Icon(
                    imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorited) Color.Red else Color.Gray
                )
            }
        }
    }
}

fun getPlacePhotoUrl(photoReference: String?, apiKey: String): String {
    return if (photoReference != null) {
        "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=$photoReference&key=$apiKey"
    } else {
        "https://via.placeholder.com/400"
    }
}