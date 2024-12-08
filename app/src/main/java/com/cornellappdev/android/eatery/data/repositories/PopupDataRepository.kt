package com.cornellappdev.android.eatery.data.repositories

import androidx.datastore.core.DataStore
import com.cornellappdev.android.eatery.Date
import com.cornellappdev.android.eatery.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PopupDataRepository @Inject constructor(
    private val userPreferencesStore: DataStore<UserPreferences>,
    appScope: CoroutineScope,
) {
    val lastShowedRatingPopupFlow = userPreferencesStore.data.map {
        with(it.lastShowedRatingPopup) {
            // Default value should be min local date
            if (year == 0) java.time.LocalDate.MIN else
                java.time.LocalDate.now().withYear(year).withDayOfMonth(day)
        }
    }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, LocalDate.MIN)

    val minDaysBetweenRatingShow =
        userPreferencesStore.data.map { it.minDaysBetweenRatingShow }
            .stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, 7)

    init {
        /**
         * This is a way of manually setting the default value for minDaysBetweenRatingShow
         * so we can multiply it for an exponential fall off.
         * Currently it doesn't seem like proto files support custom default values so this
         * is the only way to do it really.
         * See https://protobuf.dev/programming-guides/proto3/#default for more details
         */
        appScope.launch {
            userPreferencesStore.updateData {
                with(it.toBuilder()) {
                    if (it.minDaysBetweenRatingShow == 0) {
                        setMinDaysBetweenRatingShow(7).build()
                    } else {
                        build()
                    }
                }
            }
        }
    }

    suspend fun onShownRating() {
        userPreferencesStore.updateData { prefs ->
            with(prefs.toBuilder()) {
                setMinDaysBetweenRatingShow((prefs.minDaysBetweenRatingShow * 1.5).toInt())
                setLastShowedRatingPopup(
                    Date.newBuilder().setYear(2024).setMonth(10).setDay(12)
                ).build()
            }
        }
    }
}