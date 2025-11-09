package com.cornellappdev.android.eatery.data.repositories

import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.LoginRequest
import com.cornellappdev.android.eatery.data.models.ReportSendBody
import com.cornellappdev.android.eatery.data.models.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val networkApi: NetworkApi) {
    /**
     * The currently loaded user. Null if no user is logged in.
     */
    var loadedUser: User? = null

    suspend fun sendReport(issue: String, report: String, eateryID: Int?): Any =
        networkApi.sendReport(
            report = ReportSendBody(
                eatery = eateryID,
                content = "$issue: $report"
            )
        )

    /**
     * Fetches the user from backend.
     */
    suspend fun getUser(
        sessionId: String,
        deviceId: String,
        fcmToken: String
    ): User {
        val bearerToken = "Bearer $sessionId"
        val authorizedUser = networkApi.authorizeUser(
            sessionId = bearerToken,
            loginRequest = LoginRequest(deviceId = deviceId, pin = 1234, fcmToken = fcmToken)
        )
        // load accounts in case needed
        networkApi.getUserAccounts(
            sessionId = bearerToken,
            user = authorizedUser
        )
        val transactions = networkApi.getUserTransactions(
            sessionId = bearerToken,
            user = authorizedUser
        ).transactions
        val userWithData = networkApi.getUserData(
            id = authorizedUser.id
        ).copy(transactions = transactions)
        return userWithData
    }
}