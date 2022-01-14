package com.appdev.eateryblueandroid.foundations

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.appdev.eateryblueandroid.screens.home.AllEateriesViewModel
import com.appdev.eateryblueandroid.screens.home.SafeHomeScreen

class MainActivity : AppCompatActivity() {
    val allEateriesViewModel = AllEateriesViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SafeHomeScreen(allEateriesViewModel)
        }
    }
}