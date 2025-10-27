package com.cornellappdev.android.eatery.data

import com.cornellappdev.android.eatery.data.models.AccountsResponse
import com.cornellappdev.android.eatery.data.models.ApiResponse
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.Event
import com.cornellappdev.android.eatery.data.models.GetApiAccountsParams
import com.cornellappdev.android.eatery.data.models.GetApiRequestBody
import com.cornellappdev.android.eatery.data.models.GetApiResponse
import com.cornellappdev.android.eatery.data.models.GetApiTransactionHistoryParams
import com.cornellappdev.android.eatery.data.models.GetApiUserParams
import com.cornellappdev.android.eatery.data.models.ReportSendBody
import com.cornellappdev.android.eatery.data.models.TransactionsResponse
import com.cornellappdev.android.eatery.data.models.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

interface NetworkApi {
    @POST()
    suspend fun fetchUser(
        @Url url: String,
        @Body body: GetApiRequestBody<GetApiUserParams>
    ): GetApiResponse<User>

    @POST()
    suspend fun fetchAccounts(
        @Url url: String,
        @Body body: GetApiRequestBody<GetApiAccountsParams>
    ): GetApiResponse<AccountsResponse>

    @POST()
    suspend fun fetchTransactionHistory(
        @Url url: String,
        @Body body: GetApiRequestBody<GetApiTransactionHistoryParams>
    ): GetApiResponse<TransactionsResponse>

    @GET("/eatery/")
    suspend fun fetchEateries(): List<Eatery>

    @GET("/eatery/{eatery_id}")
    suspend fun fetchEatery(@Path(value = "eatery_id") eateryId: String): Eatery

    @GET("/eatery/simple")
    suspend fun fetchHomeEateries(): List<Eatery>

    @GET("/event")
    suspend fun fetchEvents(): ApiResponse<List<Event>>


    @POST("/report/")
    suspend fun sendReport(
        @Body report: ReportSendBody
    ): GetApiResponse<ReportSendBody>
}