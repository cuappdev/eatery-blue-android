package com.appdev.eateryblueandroid.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent

import com.appdev.eateryblueandroid.ui.viewmodels.EateryListViewModel
import com.appdev.eateryblueandroid.ui.components.MainScreen
import com.appdev.eateryblueandroid.ui.viewmodels.EateryDetailViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.HomeTabViewModel

class MainActivity : AppCompatActivity() {
    private val homeTabViewModel = HomeTabViewModel()
    private val eateryListViewModel = EateryListViewModel(fetchFromApi = true)
    private val eateryDetailViewModel = EateryDetailViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(
                context = this,
                homeTabViewModel = homeTabViewModel,
                eateryListViewModel = eateryListViewModel,
                eateryDetailViewModel = eateryDetailViewModel
            )
        }
    }
}