package com.cornellappdev.android.eateryblue.data.repositories

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.cornellappdev.android.eateryblue.data.NetworkApi
import com.cornellappdev.android.eateryblue.data.models.ApiResponse
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.data.models.Event
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    init {
        // Start loading backend as soon as the app initializes.
        pingAllEateries()
        pingHomeEateries()
    }

    /**
     * Makes a new call to backend for all the eatery data.
     */
    private fun pingAllEateries() {
        _eateryFlow.value = EateryApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val eateries = getAllEateries()
                _eateryFlow.value = EateryApiResponse.Success(eateries)
            } catch (_: Exception) {
                _eateryFlow.value = EateryApiResponse.Error
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
     * A map from eatery ids to the states representing their API loading calls.
     */
    private val eateryApiCache: MutableMap<Int, MutableState<EateryApiResponse<Eatery>>> =
        mutableMapOf()

    /**
     * Makes a new call to backend for the specified eatery. After calling,
     * `eateryApiCache[eateryId]` is guaranteed to contain a state actively loading that eatery's
     * data.
     */
    private fun pingEatery(eateryId: Int) {
        // If first time calling, make new state.
        if (eateryApiCache[eateryId] == null) {
            eateryApiCache[eateryId] = mutableStateOf(EateryApiResponse.Pending)
        }

        eateryApiCache[eateryId]!!.value = EateryApiResponse.Pending

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val eatery = getEatery(eateryId = eateryId)
                eateryApiCache[eateryId]!!.value = EateryApiResponse.Success(eatery)
            } catch (_: Exception) {
                eateryApiCache[eateryId]!!.value = EateryApiResponse.Error
            }
        }
    }

    /**
     * Returns the [State] representing the API call for the specified eatery.
     * If ALL eateries are already loaded, then this simply instantly returns that.
     */
    fun getEateryState(eateryId: Int): State<EateryApiResponse<Eatery>> {
        if (eateryFlow.value is EateryApiResponse.Success) {
            return mutableStateOf(
                EateryApiResponse.Success(
                    (eateryFlow.value as EateryApiResponse.Success<List<Eatery>>)
                        .data.find { it.id == eateryId }!!
                )
            )
        }

        // If not called yet or is in an error, re-ping.
        if (!eateryApiCache.contains(eateryId)
            || eateryApiCache[eateryId]!!.value is EateryApiResponse.Error
        ) {
            pingEatery(eateryId = eateryId)
        }

        return eateryApiCache[eateryId]!!
    }
}
