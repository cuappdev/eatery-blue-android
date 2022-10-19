package com.appdev.eateryblueandroid.util

import com.appdev.eateryblueandroid.ui.appContext
import com.appdev.eateryblueandroid.util.Constants.userPreferencesStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

object OnboardingRepository {
    private val onboardingFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    /** A state flow detailing a boolean corresponding to if the user has completed onboarding. */
    val onboardedFlow: StateFlow<Boolean> = onboardingFlow.asStateFlow()

    /**
     * Asynchronously begins reading from proto datastore whether the user has completed onboarding.
     * The corresponding boolean is then sent down [onboardedFlow].
     */
    fun intializeOnboardingInfo() {
        CoroutineScope(Dispatchers.IO).launch {
            val localOnboardingFlow: Flow<Boolean> = appContext.userPreferencesStore.data
                .map { userPrefs ->
                    userPrefs.hasOnboarded
                }

            localOnboardingFlow.collect { hasOnboarded ->
                onboardingFlow.value = hasOnboarded

                // Cancel once one value has been read, since this value shouldn't be changing.
                this.cancel()
            }
        }
    }

    /**
     * Saves if the user has completed onboarding to proto datastore and sends this value
     * down [onboardedFlow].
     */
    fun saveOnboardingInfo(hasOnboarded: Boolean) {
        onboardingFlow.value = hasOnboarded

        if (hasOnboarded)
            logCompletedOnboarding()

        // Save to proto Datastore
        CoroutineScope(Dispatchers.IO).launch {
            appContext.userPreferencesStore.updateData { currentPreferences ->
                currentPreferences.toBuilder()
                    .setHasOnboarded(hasOnboarded)
                    .build()
            }
        }
    }
}