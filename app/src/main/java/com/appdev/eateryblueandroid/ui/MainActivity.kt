package com.appdev.eateryblueandroid.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent

import com.appdev.eateryblueandroid.ui.viewmodels.EateryListViewModel
import com.appdev.eateryblueandroid.ui.components.MainScreen

class MainActivity : AppCompatActivity() {
    private val eateryListViewModel = EateryListViewModel(fetchFromApi = true)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //SafeHomeScreen(allEateriesViewModel)
            MainScreen(context = this, eateryListViewModel = eateryListViewModel)
        }
    }
}