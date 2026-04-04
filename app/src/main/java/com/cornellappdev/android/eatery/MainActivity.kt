package com.cornellappdev.android.eatery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.eatery.ui.navigation.NavigationSetup
import com.cornellappdev.android.eatery.ui.theme.AppColorTheme
import com.cornellappdev.android.eatery.ui.theme.ColorTheme
import com.cornellappdev.android.eatery.ui.viewmodels.ThemeViewModel
import com.cornellappdev.android.eatery.util.LockScreenOrientation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userPreferences: UserPreferencesRepository

    @Inject
    lateinit var eateryRepository: EateryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val hasOnboarded = runBlocking {
            return@runBlocking userPreferences.getHasOnboarded()
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val typography = androidx.compose.material3.Typography()
        setContent {
            LockScreenOrientation()
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val userPreferenceDark by themeViewModel.isDarkMode.collectAsState()
            val isSystemDark = isSystemInDarkTheme()
            val activeMode = when (userPreferenceDark) {
                true -> ColorTheme.darkMode
                false -> ColorTheme.lightMode
                null -> if (isSystemDark) ColorTheme.darkMode
                else ColorTheme.lightMode
            }


            AppColorTheme(activeMode) {
                NavigationSetup(hasOnboarded)
            }
        }
        val dataRefresher = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                eateryRepository.pingEateries()
            }
        }
        lifecycle.addObserver(dataRefresher)
    }
}
