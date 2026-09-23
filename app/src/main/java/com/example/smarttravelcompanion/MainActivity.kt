package com.example.smarttravelcompanion

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.smarttravelcompanion.ui.WeatherViewModel
import com.example.smarttravelcompanion.ui.theme.SmartTravelCompanionTheme
import com.example.smarttravelcompanion.utils.LocationHelper
import com.google.firebase.database.FirebaseDatabase


class MainActivity : ComponentActivity() {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("connection_test").setValue("Firebase is connected!")
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        val locationHelper = LocationHelper(this)

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            val fine = result[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarse = result[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fine || coarse) {
                locationHelper.getCurrentLocation { loc ->
                    viewModel.getTravelData(loc.latitude, loc.longitude)
                }
            }
        }

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        setContent {
            SmartTravelCompanionTheme {
                com.example.smarttravelcompanion.ui.AppNavigation(weatherViewModel = viewModel)
            }
        }
    }

}