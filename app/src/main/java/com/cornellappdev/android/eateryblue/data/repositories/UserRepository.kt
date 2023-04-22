package com.cornellappdev.android.eateryblue.data.repositories

import com.cornellappdev.android.eateryblue.BuildConfig
import com.cornellappdev.android.eateryblue.data.NetworkApi
import com.cornellappdev.android.eateryblue.data.models.*
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val networkApi: NetworkApi) {
    suspend fun sendReport(issue: String, report: String, eateryid: Int?): Any =
        networkApi.sendReport(
            report = ReportSendBody(
                eatery = eateryid,
                content = "$issue: $report"
            )
        )

    suspend fun getUser(sessionId: String): GetApiResponse<User> =
        networkApi.fetchUser(
            body = GetApiRequestBody(
                version = "1",
                method = "retrieve",
                params = GetApiUserParams(
                    sessionId = sessionId
                )
            )
        )

    suspend fun getAccount(sessionId: String, userId: String): GetApiResponse<AccountsResponse> =
        networkApi.fetchAccounts(
            body = GetApiRequestBody(
                version = "1",
                method = "retrieveAccountsByUser",
                params = GetApiAccountsParams(
                    sessionId = sessionId,
                    userId = userId
                )
            )
        )

    suspend fun getTransactionHistory(
        sessionId: String,
        userId: String,
        endDate: Date = Date(),
        startDate: Date = Date.from(
            endDate.toInstant().minus(Duration.ofDays(1460))
        )
    ): GetApiResponse<TransactionsResponse> = networkApi.fetchTransactionHistory(
        body = GetApiRequestBody(
            version = "1",
            method = "retrieveTransactionHistory",
            params = GetApiTransactionHistoryParams(
                paymentSystemType = 0,
                sessionId = sessionId,
                queryCriteria = GetApiTransactionHistoryQueryCriteria(
                    endDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(endDate),
                    startDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(startDate),
                    maxReturn = 250,
                    institutionId = BuildConfig.CORNELL_INSTITUTION_ID,
                    userId = userId
                )
            )
        )
    )
}
