package com.cornellappdev.android.eatery.data.repositories

import com.cornellappdev.android.eatery.data.NetworkApi
import com.cornellappdev.android.eatery.data.models.LoginRequest
import com.cornellappdev.android.eatery.data.models.ReportSendBody
import com.cornellappdev.android.eatery.data.models.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val networkApi: NetworkApi) {
    suspend fun sendReport(issue: String, report: String, eateryID: Int?): Any =
        networkApi.sendReport(
            report = ReportSendBody(
                eatery = eateryID,
                content = "$issue: $report"
            )
        )

    suspend fun getUser(
        sessionId: String,
        deviceId: String,
        fcmToken: String
    ): User {
        val authorizedUser = networkApi.authorizeUser(
            sessionId = "Bearer $sessionId",
            loginRequest = LoginRequest(deviceId = deviceId, pin = 1234, fcmToken = fcmToken)
        )
        return networkApi.getUserAccounts(
            sessionId = "Bearer $sessionId",
            user = authorizedUser
        )
    }

}
