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