package com.appdev.eateryblueandroid.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.appdev.eateryblueandroid.ui.viewmodels.AllEateriesViewModel
import com.appdev.eateryblueandroid.screens.home.SafeHomeScreen
import com.appdev.eateryblueandroid.ui.components.MainScreen

class MainActivity : AppCompatActivity() {
    val allEateriesViewModel = AllEateriesViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //SafeHomeScreen(allEateriesViewModel)
            MainScreen(context = this, allEateriesViewModel = allEateriesViewModel)
        }
    }
}