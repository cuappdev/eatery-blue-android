package com.cornellappdev.android.eatery.data.repositories

import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse
import com.cornellappdev.android.eatery.ui.viewmodels.state.EateryApiResponse.Success
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

    private val _eateryFlow: MutableStateFlow<EateryApiResponse<List<Eatery>>> =
        MutableStateFlow(EateryApiResponse.Pending)

    /**
     * A [StateFlow] emitting [EateryApiResponse]s for lists of ALL eateries, if loaded successfully.
     */
    val eateryFlow = _eateryFlow.asStateFlow()

    /**
     * A map from eatery ids to the states representing their API loading calls.
     */
    private val eateryApiCache: MutableStateFlow<Map<Int, EateryApiResponse<Eatery>>> =
        MutableStateFlow(mapOf<Int, EateryApiResponse<Eatery>>().withDefault { EateryApiResponse.Error })

    init {
        // Start loading backend as soon as the app initializes.
        pingEateries()
    }

    /**
     * Makes a new call to backend for all the eatery data.
     */
    fun pingEateries() {
        _eateryFlow.value = EateryApiResponse.Pending
        eateryApiCache.update { map ->
            map.mapValues { EateryApiResponse.Pending }
                .withDefault { EateryApiResponse.Error }
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val eateries = getAllEateries()
                _eateryFlow.value = Success(eateries)
                eateryApiCache.update {
                    eateries.filter { it.id != null }
                        .associate { it.id!! to Success(it) }
                        .withDefault { EateryApiResponse.Error }
                }
            } catch (_: Exception) {
                _eateryFlow.value = EateryApiResponse.Error
                eateryApiCache.update {
                    emptyMap<Int, EateryApiResponse<Eatery>>().withDefault { EateryApiResponse.Error }
                }
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
                updateCache(eateryId, Success(eatery))
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
