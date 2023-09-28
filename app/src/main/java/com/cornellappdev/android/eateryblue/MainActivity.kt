package com.cornellappdev.android.eateryblue

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.cornellappdev.android.eateryblue.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eateryblue.ui.navigation.NavigationSetup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userPreferences: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val hasOnboarded = runBlocking {
            Log.d("ONBOARDED?????", userPreferences.getHasOnboarded().toString())
            return@runBlocking userPreferences.getHasOnboarded()
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Want to eventually switch over to this typography
        val typography = androidx.compose.material3.Typography()
        setContent {
            androidx.compose.material3.MaterialTheme {
                NavigationSetup(hasOnboarded)
            }
        }
    }
}
