package com.cornellappdev.android.eatery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.cornellappdev.android.eatery.data.repositories.EateryRepository
import com.cornellappdev.android.eatery.data.repositories.UserRepository
import com.cornellappdev.android.eatery.ui.navigation.NavigationSetup
import com.cornellappdev.android.eatery.util.LockScreenOrientation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var eateryRepository: EateryRepository

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val hasOnboarded = runBlocking { userRepository.hasOnboarded() }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val typography = androidx.compose.material3.Typography()
        setContent {
            LockScreenOrientation()

            androidx.compose.material3.MaterialTheme(typography = typography) {
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
        runBlocking {
            configureTokens()
            userRepository.updateFavorites()
            userRepository.markTokensAsConfigured()
        }
    }

    private suspend fun configureTokens() {
        if (!userRepository.hasLaunchedBefore()) {
            userRepository.registerDevice()
        }
        userRepository.getTokens()
    }
}
