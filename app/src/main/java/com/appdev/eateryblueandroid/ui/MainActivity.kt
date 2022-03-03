package com.appdev.eateryblueandroid.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.appdev.eateryblueandroid.ui.components.login.LoginWebView

import com.appdev.eateryblueandroid.ui.navigation.MainScreen
import com.appdev.eateryblueandroid.ui.viewmodels.*
import com.appdev.eateryblueandroid.util.appContext
import com.appdev.eateryblueandroid.util.initializeFavoriteMap

// TODO: State management with ViewModels is bad. We should switch to RXJava.
class MainActivity : AppCompatActivity() {
    private val homeTabViewModel = HomeTabViewModel()
    private val eateryListViewModel = HomeViewModel(fetchFromApi = true)
    private val expandedSectionViewModel = ExpandedSectionViewModel()
    private val eateryDetailViewModel = EateryDetailViewModel()

    private val profileViewModel = ProfileViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(
                context = this,
                homeTabViewModel = homeTabViewModel,
                homeViewModel = eateryListViewModel,
                expandedSectionViewModel = expandedSectionViewModel,
                eateryDetailViewModel = eateryDetailViewModel,

                profileViewModel = profileViewModel
            )
            LoginWebView(
                profileViewModel = profileViewModel,
            )
        }
        appContext = this
        initializeFavoriteMap()
    }
}