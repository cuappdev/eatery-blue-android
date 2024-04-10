package com.cornellappdev.android.eatery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.ui.navigation.NavigationSetup
import com.cornellappdev.android.eatery.util.LockScreenOrientation
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
            return@runBlocking userPreferences.getHasOnboarded()
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Want to eventually switch over to this typography
        val typography = androidx.compose.material3.Typography()
        setContent {
            LockScreenOrientation()

            androidx.compose.material3.MaterialTheme {
                NavigationSetup(hasOnboarded)
            }
        }
    }
}
