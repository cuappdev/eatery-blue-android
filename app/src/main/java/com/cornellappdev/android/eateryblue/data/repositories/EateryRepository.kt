package com.cornellappdev.android.eateryblue.data.repositories

import com.cornellappdev.android.eateryblue.data.NetworkApi
import com.cornellappdev.android.eateryblue.data.models.ApiResponse
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.data.models.Event
import com.cornellappdev.android.eateryblue.ui.viewmodels.state.EateryApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EateryRepository @Inject constructor(private val networkApi: NetworkApi) {
    suspend fun getAllEateries(): List<Eatery> =
        networkApi.fetchEateries()

    suspend fun getEatery(eateryId: Int): Eatery =
        networkApi.fetchEatery(eateryId = eateryId.toString())

    suspend fun getHomeEateries(): List<Eatery> =
        networkApi.fetchHomeEateries()

    suspend fun getAllEvents(): ApiResponse<List<Event>> =
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
    fun pingAllEateries() {
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
}
