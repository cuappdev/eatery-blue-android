package com.appdev.eateryblueandroid.networking

import com.appdev.eateryblueandroid.models.ApiResponse
import com.appdev.eateryblueandroid.models.Eatery
import retrofit2.http.GET

interface Api {
    @GET("/api")
    suspend fun fetchEateries() : ApiResponse<List<Eatery>>
}