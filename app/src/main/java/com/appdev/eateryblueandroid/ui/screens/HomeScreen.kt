package com.appdev.eateryblueandroid.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.models.EaterySection
import com.appdev.eateryblueandroid.ui.appContext
import com.appdev.eateryblueandroid.ui.components.general.TopBar
import com.appdev.eateryblueandroid.ui.components.home.Main
import com.appdev.eateryblueandroid.ui.components.home.MainLoading
import com.appdev.eateryblueandroid.ui.viewmodels.BottomSheetViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.HomeViewModel
import com.appdev.eateryblueandroid.util.Constants.userPreferencesStore
import com.appdev.eateryblueandroid.util.LocationHandler
import com.codelab.android.datastore.PermissionSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    selectEatery: (eatery: Eatery) -> Unit,
    selectSection: (eaterySection: EaterySection) -> Unit,
    selectSearch: () -> Unit,
    scrollState: LazyListState,
    bottomSheetViewModel: BottomSheetViewModel
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
                LocationHandler.locationPermissionRequested(true)
            }
            else -> {
                LocationHandler.locationPermissionRequested(true)
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
                CoroutineScope(Dispatchers.IO).launch {
                    val permissionsFlow: Flow<PermissionSettings> =
                        appContext.userPreferencesStore.data.map {
                            it.permissionSettings
                        }
                    permissionsFlow.collect {
                        if (!it.locationAccess) {
                            locationPermissionRequest.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    }
                }

            }
        }
    }
    Column {
        TopBar(
            label = "Eatery",
            expanded = scrollState.firstVisibleItemIndex == 0,
            eateryIcon = true,
            rightIcon = painterResource(id = R.drawable.ic_search)
        )
        val state = homeViewModel.state.collectAsState()
        val filterScrollState = rememberLazyListState()
        Crossfade(targetState = state.value, animationSpec = tween(700)) {
            when (it) {
                is HomeViewModel.State.Loading ->
                    MainLoading(scrollState)
                /* TODO 1: Selecting new filters is causing complete recomposition of the screen,
                *   meaning the crossfade carries over. This doesn't look great and is kinda slow.
                *   The reason a recomposition is happening is that HomeViewModel.filters is updated,
                *   which causes a whole recomposition of Main() (I think...)
                *
                *  TODO 2: Upon recomposition, the filter scroll state is not saved. Find out why, and
                *   make the scroll save.
                * */
                is HomeViewModel.State.Data -> {
                    Main(
                        scrollState = scrollState,
                        eateries = it.eateries,
                        sections = it.sections,
                        filters = it.filters,
                        setFilters = { s -> homeViewModel.updateFilters(s) },
                        selectEatery = selectEatery,
                        selectSection = selectSection,
                        selectSearch = selectSearch,
                        bottomSheetViewModel = bottomSheetViewModel,
                        filterState = filterScrollState
                    )
                }
                is HomeViewModel.State.Failure ->
                    Text("FAILURE")
            }
        }
    }
}