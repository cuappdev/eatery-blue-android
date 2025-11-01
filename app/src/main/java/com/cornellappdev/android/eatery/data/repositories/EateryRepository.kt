package com.cornellappdev.android.eatery.data.repositories

import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EateryRepository @Inject constructor(private val networkApi: NetworkApi) {
    enum class Screen {
        HOME,
        DETAILS,
        UPCOMING
    }

    private var currentScreen: Screen = Screen.HOME
    private var lastEateryPinged: Int? = null
    private var lastDayRequested: Int? = null

    fun changeScreen(screen: Screen) {
        currentScreen = screen
    }

    private suspend fun getEatery(eateryId: Int): Eatery =
        networkApi.fetchEatery(eateryId = eateryId.toString())

    private suspend fun getHomeEateries(): List<Eatery> =
        networkApi.fetchHomeEateries()

    private suspend fun getUpcomingEateries(day: Int): List<Eatery> =
        networkApi.fetchEateriesByDay(dayId = day)

    private val _eateryFlow: MutableStateFlow<EateryApiResponse<List<Eatery>>> =
        MutableStateFlow(EateryApiResponse.Pending)

    /**
     * A [StateFlow] emitting [EateryApiResponse]s for lists of ALL eateries, if loaded successfully.
     */
    val eateryFlow = _eateryFlow.asStateFlow()

    private val _homeEateryFlow: MutableStateFlow<EateryApiResponse<List<Eatery>>> =
        MutableStateFlow(EateryApiResponse.Pending)

    /**
     * A [StateFlow] emitting [EateryApiResponse]s for lists of home eateries.
     */
    val homeEateryFlow = _homeEateryFlow.asStateFlow()

    private val _upcomingEateriesFlow: MutableStateFlow<EateryApiResponse<List<Eatery>>> =
        MutableStateFlow(EateryApiResponse.Pending)

    val upcomingEateriesFlow = _upcomingEateriesFlow.asStateFlow()

    /**
     * A map from eatery ids to the states representing their API loading calls.
     */
    private val eateryDetailsCache: MutableStateFlow<Map<Int, EateryApiResponse<Eatery>>> =
        MutableStateFlow(mapOf<Int, EateryApiResponse<Eatery>>().withDefault { EateryApiResponse.Error })

    private val upcomingEateriesCache: MutableStateFlow<Map<Int, EateryApiResponse<List<Eatery>>>> =
        MutableStateFlow(mapOf<Int, EateryApiResponse<List<Eatery>>>().withDefault { EateryApiResponse.Error })

    init {
        // Start loading backend as soon as the app initializes.
        pingHomeEateries()
    }

    /**
     * Refreshes the data for the current screen and resets all other stale cache data.
     */
    fun refresh() {
        // Re-ping based on current screen and clear all other caches.
        when (currentScreen) {
            Screen.HOME -> {
                pingHomeEateries()
                eateryDetailsCache.value = emptyMap()
                upcomingEateriesCache.value = emptyMap()
            }

            Screen.DETAILS -> lastEateryPinged?.let {
                eateryDetailsCache.value = emptyMap()
                pingEatery(it)
                upcomingEateriesCache.value = emptyMap()
            }

            Screen.UPCOMING -> lastDayRequested?.let {
                upcomingEateriesCache.value = emptyMap()
                pingUpcomingMenu(it)
                eateryDetailsCache.value = emptyMap()
            }
        }
    }

    /**
     * Makes a new call to backend for all the abridged home eatery data.
     */
    fun pingHomeEateries() {
        _homeEateryFlow.value = EateryApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val eateries = getHomeEateries()
                _homeEateryFlow.value = EateryApiResponse.Success(eateries)
            } catch (_: Exception) {
                _homeEateryFlow.value = EateryApiResponse.Error
            }
        }
    }

    /**
     * Retrieves upcoming eatery data for the specified day, either from cache or by making a new
     * backend call.
     */
    fun retrieveUpcomingMenu(day: Int) {
        lastDayRequested = day
        val cachedResponse = upcomingEateriesCache.value[day]
        if (cachedResponse != null) {
            _upcomingEateriesFlow.value = cachedResponse
            if (cachedResponse is EateryApiResponse.Success) {
                return
            }
        }
        pingUpcomingMenu(day)
    }

    /**
     * Makes a new call to backend for upcoming eatery data for the specified day.
     * Only updates cache for [day].
     */
    private fun pingUpcomingMenu(day: Int) {
        lastDayRequested = day
        _upcomingEateriesFlow.value = EateryApiResponse.Pending
        upcomingEateriesCache.update { map ->
            (map + (day to EateryApiResponse.Pending))
                .withDefault { EateryApiResponse.Error }
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val eateries = getUpcomingEateries(day)
                _upcomingEateriesFlow.value = EateryApiResponse.Success(eateries)
                upcomingEateriesCache.update { map ->
                    map + (day to EateryApiResponse.Success(eateries))
                }
            } catch (_: Exception) {
                _upcomingEateriesFlow.value = EateryApiResponse.Error
            }
        }
    }

    /**
     * Makes a new call to backend for the specified eatery. After calling,
     * `eateryApiCache[eateryId]` is guaranteed to contain a state actively loading that eatery's
     * data.
     */
    private fun pingEatery(eateryId: Int) {
        lastEateryPinged = eateryId
        // If first time calling, make new state.
        updateCache(eateryId, EateryApiResponse.Pending)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val eatery = getEatery(eateryId = eateryId)
                updateCache(eateryId, EateryApiResponse.Success(eatery))
            } catch (_: Exception) {
                updateCache(eateryId, EateryApiResponse.Error)
            }
        }
    }

    private fun updateCache(eateryId: Int, response: EateryApiResponse<Eatery>) {
        eateryDetailsCache.update {
            (it + (eateryId to response)).withDefault { EateryApiResponse.Error }
        }
    }

    /**
     * Returns the [StateFlow] representing the API call for the specified eatery.
     * If ALL eateries are already loaded, then this simply instantly returns that.
     */
    fun getEateryFlow(eateryId: Int): Flow<EateryApiResponse<Eatery>> {
        if (!eateryDetailsCache.value.contains(eateryId)) {
            pingEatery(eateryId)
        }
        return eateryDetailsCache.map { it.getValue(eateryId) }
    }
}
