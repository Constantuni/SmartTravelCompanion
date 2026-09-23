package com.example.smarttravelcompanion.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun AppNavigation(weatherViewModel: WeatherViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "weather"
    ) {
        composable("weather") {
            WeatherScreen(
                weatherViewModel = weatherViewModel,
                onGoToFavorites = {
                    navController.navigate("favorites")
                }
            )
        }

        composable("favorites") {
            FavoritesScreen(
                viewModel = weatherViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}