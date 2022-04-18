package com.appdev.eateryblueandroid.networking.internal

import com.appdev.eateryblueandroid.models.ApiResponse
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.models.ReportSendBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Api {
    @GET("api")
    suspend fun fetchEateries(): ApiResponse<List<Eatery>>

    @POST("api/report")
    suspend fun sendReport(
        @Body report: ReportSendBody
    ): ApiResponse<String>
}