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

    init {
        // Start loading backend as soon as the app initializes.
        pingBackend()
    }

    /**
     * Makes a new call to backend for all the eatery data.
     */
    fun pingBackend() {
        // TODO: Add some way of letting a user re-ping by calling this method when an error occurs.
        //  There should be some sort of UI (e.g. pull up to refresh, error state w/ button, etc.)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val eateries = getAllEateries()
                _eateryFlow.value = EateryApiResponse.Success(eateries)
            } catch (_: Exception) {
                _eateryFlow.value = EateryApiResponse.Error
            }
        }
    }
}
