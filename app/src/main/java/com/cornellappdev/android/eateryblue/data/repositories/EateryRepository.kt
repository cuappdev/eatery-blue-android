package com.cornellappdev.android.eateryblue.data.repositories

import com.cornellappdev.android.eateryblue.data.NetworkApi
import com.cornellappdev.android.eateryblue.data.models.ApiResponse
import com.cornellappdev.android.eateryblue.data.models.Eatery
import com.cornellappdev.android.eateryblue.data.models.Event
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EateryRepository @Inject constructor(private val networkApi: NetworkApi) {
    suspend fun getAllEateries(): ApiResponse<List<Eatery>> =
        networkApi.fetchEateries()

    suspend fun getAllEvents(): ApiResponse<List<Event>> =
        networkApi.fetchEvents()
}
