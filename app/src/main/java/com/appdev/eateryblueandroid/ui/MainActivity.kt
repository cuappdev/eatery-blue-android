package com.appdev.eateryblueandroid.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.appdev.eateryblueandroid.ui.components.general.BottomSheet
import com.appdev.eateryblueandroid.ui.components.login.LoginWebView
import com.appdev.eateryblueandroid.ui.navigation.MainScreen
import com.appdev.eateryblueandroid.ui.viewmodels.*
import com.appdev.eateryblueandroid.util.LoginRepository
import com.appdev.eateryblueandroid.util.OnboardingRepository
import com.appdev.eateryblueandroid.util.RecentSearchesRepository
import com.appdev.eateryblueandroid.util.initializeNotificationsSettings
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

lateinit var appContext: Context
val analytics = Firebase.analytics

// TODO: State management with ViewModels is bad. We should switch to RXJava.
class MainActivity : AppCompatActivity() {
    private val homeTabViewModel = HomeTabViewModel()
    private val eateryListViewModel = HomeViewModel(fetchFromApi = true)
    private val expandedSectionViewModel = ExpandedSectionViewModel()
    private val eateryDetailViewModel = EateryDetailViewModel()
    private val profileEateryDetailViewModel = EateryDetailViewModel()

    private val searchViewModel = SearchViewModel()
    private val profileViewModel = ProfileViewModel()
    private val bottomSheetViewModel = BottomSheetViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContext = this

        LoginRepository.initializeLoginData()
        RecentSearchesRepository.initializeRecentSearches()
        initializeNotificationsSettings()
        OnboardingRepository.intializeOnboardingInfo()
        profileViewModel.watchForAutoLogin()

        setContent {
            MainScreen(
                context = this,
                homeTabViewModel = homeTabViewModel,
                homeViewModel = eateryListViewModel,
                expandedSectionViewModel = expandedSectionViewModel,
                eateryDetailViewModel = eateryDetailViewModel,
                profileViewModel = profileViewModel,
                bottomSheetViewModel = bottomSheetViewModel,
                profileEateryDetailViewModel = profileEateryDetailViewModel,
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