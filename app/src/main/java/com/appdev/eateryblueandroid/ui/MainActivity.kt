package com.appdev.eateryblueandroid.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.appdev.eateryblueandroid.screens.home.HomeScreen
import com.appdev.eateryblueandroid.ui.screens.EateryDetailScreen
import com.appdev.eateryblueandroid.ui.viewmodels.EateryListViewModel
import com.appdev.eateryblueandroid.ui.viewmodels.EateryViewModel

class MainActivity : AppCompatActivity() {
    private val eateryListViewModel = EateryListViewModel(fetchFromApi = true)
    private val eateryViewModel = EateryViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EateryDetailScreen(eateryViewModel = eateryViewModel)
        }
    }
}