package com.appdev.eateryblueandroid.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.appdev.eateryblueandroid.ui.components.general.BottomSheet
import com.appdev.eateryblueandroid.ui.components.login.LoginWebView

import com.appdev.eateryblueandroid.ui.navigation.MainScreen
import com.appdev.eateryblueandroid.ui.viewmodels.*
import com.appdev.eateryblueandroid.util.*

// TODO: State management with ViewModels is bad. We should switch to RXJava.
class MainActivity : AppCompatActivity() {
    private val homeTabViewModel = HomeTabViewModel()
    private val eateryListViewModel = HomeViewModel(fetchFromApi = true)
    private val expandedSectionViewModel = ExpandedSectionViewModel()
    private val eateryDetailViewModel = EateryDetailViewModel()
    private val searchViewModel = SearchViewModel()
    private val profileViewModel = ProfileViewModel()
    private val bottomSheetViewModel = BottomSheetViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContext = this
        checkProfileCache()
        initializeLoginData()
        initializeRecentSearches()
        setContent {
            MainScreen(
                context = this,
                homeTabViewModel = homeTabViewModel,
                homeViewModel = eateryListViewModel,
                expandedSectionViewModel = expandedSectionViewModel,
                eateryDetailViewModel = eateryDetailViewModel,
                profileViewModel = profileViewModel,
                bottomSheetViewModel = bottomSheetViewModel,
                searchViewModel = searchViewModel,
            )
            LoginWebView(
                profileViewModel = profileViewModel,
            )
            BottomSheet(
                bottomSheetViewModel = bottomSheetViewModel
            )
        }
    }
}