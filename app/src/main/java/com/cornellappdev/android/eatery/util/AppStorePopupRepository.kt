package com.cornellappdev.android.eatery.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.cornellappdev.android.eatery.data.repositories.PopupDataRepository
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppStorePopupRepository @Inject constructor(
    val userPreferencesRepository: UserPreferencesRepository,
    private val popupDataRepository: PopupDataRepository,
    private val appScope: CoroutineScope,
) {
    private val popupEventFlow = MutableStateFlow(false)
    val popupShowing = popupEventFlow.asStateFlow()

    /**
     * This function requests that the app store popup repository shows a rating popup to the user.
     * This request will not succeed if the number of days since the user has been shown the rating
     * prompt is less than the minimum number of days in between when the rating pop up should show.
     * This minimum days number is increased exponentially as the rating prompt is successfully shown.
     */
    fun requestRatingPopup() {
        val lastShowedRatingPopup =
            popupDataRepository.lastShowedRatingPopupFlow.value.atStartOfDay()
        val now = LocalDate.now().atStartOfDay()
        val daysSinceRatingShown = Duration.between(
            lastShowedRatingPopup,
            now,
        ).toDays()
        if (
            daysSinceRatingShown >= popupDataRepository.minDaysBetweenRatingShow.value
        ) {
            popupEventFlow.update { true }
            appScope.launch {
                popupDataRepository.onShownRating()
            }
        }
    }

    fun dismissPopup() {
        popupEventFlow.update { false }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppStorePopupManagerEntryPoint {
    fun getAppStorePopupManager(): AppStorePopupRepository
}

@Composable
fun appStorePopupRepository(): AppStorePopupRepository {
    val context = LocalContext.current
    val appStorePopupManager = EntryPointAccessors.fromApplication<AppStorePopupManagerEntryPoint>(
        context.applicationContext,
    ).getAppStorePopupManager()
    return appStorePopupManager
}