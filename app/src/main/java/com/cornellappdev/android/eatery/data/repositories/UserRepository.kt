package com.cornellappdev.android.eatery.data.repositories

import com.cornellappdev.android.eatery.BuildConfig
import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.AccountsResponse
import com.cornellappdev.android.eatery.data.models.GetApiAccountsParams
import com.cornellappdev.android.eatery.data.models.GetApiRequestBody
import com.cornellappdev.android.eatery.data.models.GetApiResponse
import com.cornellappdev.android.eatery.data.models.GetApiTransactionHistoryParams
import com.cornellappdev.android.eatery.data.models.GetApiTransactionHistoryQueryCriteria
import com.cornellappdev.android.eatery.data.models.GetApiUserParams
import com.cornellappdev.android.eatery.data.models.ReportSendBody
import com.cornellappdev.android.eatery.data.models.TransactionsResponse
import com.cornellappdev.android.eatery.data.models.User
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.Date
import java.util.Locale
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
            url = BuildConfig.GET_BACKEND_URL + "user",
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
            url = BuildConfig.GET_BACKEND_URL + "commerce",
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
        url = BuildConfig.GET_BACKEND_URL + "commerce",
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
