package com.cornellappdev.android.eateryblue.data

import com.cornellappdev.android.eateryblue.BuildConfig
import com.cornellappdev.android.eateryblue.data.models.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*

interface NetworkApi {
    @POST("user")
    suspend fun fetchUser(
        @Body body: GetApiRequestBody<GetApiUserParams>
    ): GetApiResponse<User>

    @POST("commerce")
    suspend fun fetchAccounts(
        @Body body: GetApiRequestBody<GetApiAccountsParams>
    ): GetApiResponse<AccountsResponse>

    @POST("commerce")
    suspend fun fetchTransactionHistory(
        @Body body: GetApiRequestBody<GetApiTransactionHistoryParams>
    ): GetApiResponse<TransactionsResponse>

    @GET("/eatery")
    suspend fun fetchEateries(): ApiResponse<List<Eatery>>

    @GET("/event")
    suspend fun fetchEvents(): ApiResponse<List<Event>>


    @POST("api/report")
    suspend fun sendReport(
        @Body report: ReportSendBody
    ): ApiResponse<String>
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
