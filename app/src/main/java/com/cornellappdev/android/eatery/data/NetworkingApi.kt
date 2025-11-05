package com.cornellappdev.android.eatery.data

import com.cornellappdev.android.eatery.data.models.AuthorizedUser
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.GetApiResponse
import com.cornellappdev.android.eatery.data.models.LoginRequest
import com.cornellappdev.android.eatery.data.models.ReportSendBody
import com.cornellappdev.android.eatery.data.models.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface NetworkApi {
    @GET("/eatery/")
    suspend fun fetchEateries(): List<Eatery>

    @GET("/eatery/{eatery_id}")
    suspend fun fetchEatery(@Path(value = "eatery_id") eateryId: String): Eatery

    @GET("/eatery/simple")
    suspend fun fetchHomeEateries(): List<Eatery>

    @POST("/report/")
    suspend fun sendReport(
        @Body report: ReportSendBody
    ): GetApiResponse<ReportSendBody>

    @POST("/user/authorize/")
    suspend fun authorizeUser(
        @Header("Authorization") sessionId: String,
        @Body loginRequest: LoginRequest
    ): AuthorizedUser

    @POST("/user/accounts/")
    suspend fun getUserAccounts(
        @Header("Authorization") sessionId: String,
        @Body user: AuthorizedUser
    ): User
}