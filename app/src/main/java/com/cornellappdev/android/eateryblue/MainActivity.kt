package com.cornellappdev.android.eateryblue

import android.os.Bundle
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
            return@runBlocking userPreferences.getHasOnboarded()
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        // TODO test keyboard
        //window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val typography = androidx.compose.material3.Typography()
        setContent {
            androidx.compose.material3.MaterialTheme() {
                NavigationSetup(hasOnboarded)
            }
        }
    }
}
