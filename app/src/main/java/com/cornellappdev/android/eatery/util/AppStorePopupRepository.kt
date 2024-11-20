package com.cornellappdev.android.eatery.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.cornellappdev.android.eatery.data.repositories.UserPreferencesRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppStorePopupRepository @Inject constructor(
    val userPreferencesRepository: UserPreferencesRepository,
) {
    private val popupEventFlow = MutableStateFlow(false)
    val popupShowing = popupEventFlow.asStateFlow()

    suspend fun requestRatingPopup() {
        if (
            Duration.between(
                userPreferencesRepository.lastShowedRatingPopupFlow.value.atStartOfDay(),
                LocalDate.now().atStartOfDay(),
            ).toDays() >= userPreferencesRepository.minDaysBetweenRatingShow.value
        ) {
            popupEventFlow.update { true }
            userPreferencesRepository.onShownRating()
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