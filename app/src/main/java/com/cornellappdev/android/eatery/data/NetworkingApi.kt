package com.cornellappdev.android.eatery.data

import com.cornellappdev.android.eatery.BuildConfig
import com.cornellappdev.android.eatery.data.models.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*

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

fun generateUserBody(sessionId: String): GetApiRequestBody<GetApiUserParams> {
    return GetApiRequestBody(
        version = "1",
        method = "retrieve",
        params = GetApiUserParams(
            sessionId = sessionId
        )
    )
}

fun generateAccountsBody(
    sessionId: String,
    userId: String
): GetApiRequestBody<GetApiAccountsParams> {
    return GetApiRequestBody(
        version = "1",
        method = "retrieveAccountsByUser",
        params = GetApiAccountsParams(
            sessionId = sessionId,
            userId = userId
        )
    )
}

fun generateTransactionHistoryBody(
    sessionId: String, userId: String, endDate: Date
): GetApiRequestBody<GetApiTransactionHistoryParams> {
    val startDate = Date.from(endDate.toInstant().minus(Duration.ofDays(5000)))
    return GetApiRequestBody(
        version = "1",
        method = "retrieveTransactionHistory",
        params = GetApiTransactionHistoryParams(
            paymentSystemType = 0,
            sessionId = sessionId,
            queryCriteria = GetApiTransactionHistoryQueryCriteria(
                endDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(endDate),
                startDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(startDate),
                maxReturn = 5000,
                institutionId = BuildConfig.CORNELL_INSTITUTION_ID,
                userId = userId
            )
        )
    )
}
