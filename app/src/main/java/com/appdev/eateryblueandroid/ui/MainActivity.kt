package com.appdev.eateryblueandroid.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.appdev.eateryblueandroid.ui.components.general.BottomSheet
import com.appdev.eateryblueandroid.ui.components.login.LoginWebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.appdev.eateryblueandroid.ui.components.core.Text

import com.appdev.eateryblueandroid.ui.navigation.MainScreen
import com.appdev.eateryblueandroid.ui.viewmodels.*

class MainActivity : AppCompatActivity() {
    private val homeTabViewModel = HomeTabViewModel()
    private val eateryListViewModel = HomeViewModel(fetchFromApi = true)
    private val expandedSectionViewModel = ExpandedSectionViewModel()
    private val eateryDetailViewModel = EateryDetailViewModel()

    private val profileViewModel = ProfileViewModel()
    private val bottomSheetViewModel = BottomSheetViewModel()
    private val searchViewModel = SearchViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(
                context = this,
                homeTabViewModel = homeTabViewModel,
                homeViewModel = eateryListViewModel,
                expandedSectionViewModel = expandedSectionViewModel,
                eateryDetailViewModel = eateryDetailViewModel,
                profileViewModel = profileViewModel,
                bottomSheetViewModel = bottomSheetViewModel
            )
            LoginWebView(
                profileViewModel = profileViewModel,
            )
            BottomSheet(
                bottomSheetViewModel = bottomSheetViewModel
               
            ),
             searchViewModel = searchViewModel,
        }
    }

}
