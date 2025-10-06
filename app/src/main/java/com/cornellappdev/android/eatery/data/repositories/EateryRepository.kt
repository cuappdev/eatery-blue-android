package com.cornellappdev.android.eatery.data.repositories

import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.ApiResponse
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.Event
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
    private suspend fun getAllEateries(): List<Eatery> =
        networkApi.fetchEateries()

    private suspend fun getEatery(eateryId: Int): Eatery =
        networkApi.fetchEatery(eateryId = eateryId.toString())

    private suspend fun getHomeEateries(): List<Eatery> =
        networkApi.fetchHomeEateries()

    private suspend fun getAllEvents(): ApiResponse<List<Event>> =
        networkApi.fetchEvents()

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

    /**
     * A map from eatery ids to the states representing their API loading calls.
     */
    private val eateryApiCache: MutableStateFlow<Map<Int, EateryApiResponse<Eatery>>> =
        MutableStateFlow(mapOf<Int, EateryApiResponse<Eatery>>().withDefault { EateryApiResponse.Error })

    init {
        // Start loading backend as soon as the app initializes.
        pingEateries()
    }

    fun pingEateries() {
        pingAllEateries()
        pingHomeEateries()
    }

    /**
     * Makes a new call to backend for all the eatery data.
     */
    private fun pingAllEateries() {
        _eateryFlow.value = EateryApiResponse.Pending
        eateryApiCache.update { map ->
            map.mapValues { EateryApiResponse.Pending }
                .withDefault { EateryApiResponse.Error }
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val eateries = getAllEateries()
                _eateryFlow.value = EateryApiResponse.Success(eateries)
                eateryApiCache.update { map ->
                    eateries.filter { it.id != null }
                        .associate { it.id!! to EateryApiResponse.Success(it) }
                        .withDefault { EateryApiResponse.Error }
                }
            } catch (_: Exception) {
                _eateryFlow.value = EateryApiResponse.Error
                eateryApiCache.update { map ->
                    map.mapValues { EateryApiResponse.Error }
                        .withDefault { EateryApiResponse.Error }
                }
            }
        }
    }

    /**
     * Makes a new call to backend for all the abridged home eatery data.
     */
    private fun pingHomeEateries() {
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
     * Makes a new call to backend for the specified eatery. After calling,
     * `eateryApiCache[eateryId]` is guaranteed to contain a state actively loading that eatery's
     * data.
     */
    private fun pingEatery(eateryId: Int) {
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
        eateryApiCache.update {
            (it + (eateryId to response)).withDefault { EateryApiResponse.Error }
        }
    }

    /**
     * Returns the [StateFlow] representing the API call for the specified eatery.
     * If ALL eateries are already loaded, then this simply instantly returns that.
     */
    fun getEateryFlow(eateryId: Int): Flow<EateryApiResponse<Eatery>> {
        if (!eateryApiCache.value.contains(eateryId)) {
            pingEatery(eateryId)
        }
        return eateryApiCache.map { it.getValue(eateryId) }
    }
}
