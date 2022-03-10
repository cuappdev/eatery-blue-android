package com.appdev.eateryblueandroid.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import com.appdev.eateryblueandroid.ui.viewmodels.HomeViewModel
import androidx.compose.foundation.lazy.LazyListState
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.models.EaterySection
import com.appdev.eateryblueandroid.ui.components.home.Main
import com.appdev.eateryblueandroid.ui.components.home.TopBar

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    selectEatery: (eatery: Eatery) -> Unit,
    selectSection: (eaterySection: EaterySection) -> Unit,
    selectSearch: () -> Unit,
    scrollState: LazyListState
) {
    val context = LocalContext.current
    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                LocationHandler.instantiate(context)
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                /* TODO: Handle insufficient permission */
            }
            else -> {
                /* TODO: Handle insufficient permission */
            }
        }
    }
    SideEffect {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                LocationHandler.instantiate(context)
            }
            else -> {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }
    Column {
        TopBar(scrollState = scrollState)
        val state = homeViewModel.state.collectAsState()
        state.value.let {
            when(it) {
                is HomeViewModel.State.Loading ->
                    Box {}
                is HomeViewModel.State.Data ->
                    Main(
                        scrollState = scrollState,
                        eateries = it.eateries,
                        sections = it.sections,
                        selectEatery = selectEatery,
                        selectSection = selectSection,
                        selectSearch = selectSearch
                    )
                is HomeViewModel.State.Failure ->
                    Text("FAILURE")
            }
        }
    }
}