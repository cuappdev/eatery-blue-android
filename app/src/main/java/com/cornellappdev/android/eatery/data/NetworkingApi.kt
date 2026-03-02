package com.cornellappdev.android.eatery.data

import com.cornellappdev.android.eatery.data.models.AuthTokens
import com.cornellappdev.android.eatery.data.models.DeviceId
import com.cornellappdev.android.eatery.data.models.Eatery
import com.cornellappdev.android.eatery.data.models.FavoriteEatery
import com.cornellappdev.android.eatery.data.models.FavoriteItem
import com.cornellappdev.android.eatery.data.models.FcmToken
import com.cornellappdev.android.eatery.data.models.Financials
import com.cornellappdev.android.eatery.data.models.GetApiResponse
import com.cornellappdev.android.eatery.data.models.LoginPIN
import com.cornellappdev.android.eatery.data.models.LoginRequest
import com.cornellappdev.android.eatery.data.models.Match
import com.cornellappdev.android.eatery.data.models.RefreshRequest
import com.cornellappdev.android.eatery.data.models.ReportSendBody
import com.cornellappdev.android.eatery.data.models.SessionID
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface NetworkApi {
    @GET("/eateries/")
    suspend fun fetchEateries(): List<Eatery>

    @GET("/eateries/{eatery_id}")
    suspend fun fetchEatery(@Path(value = "eatery_id") eateryId: String): Eatery

    @POST("/report/")
    suspend fun sendReport(
        @Body report: ReportSendBody
    ): GetApiResponse<ReportSendBody>

    /**
     * Called on app launch to get session tokens based on UUID
     */
    @POST("/auth/verify-token")
    suspend fun verifyToken(
        @Body deviceId: DeviceId
    ): AuthTokens

    /**
     * Get a new pair of tokens
     */
    @POST("/auth/refresh-token")
    suspend fun refreshToken(
        @Body refreshRequest: RefreshRequest
    ): AuthTokens

    /* All [accessToken]s should start with "Bearer".
    * E.g., Authorization: Bearer a97syd9a77asydan9s
    * */

    @POST("/users/fcm-token")
    suspend fun enableNotifications(
        @Header("Authorization") accessToken: String,
        @Body token: FcmToken
    )

    @DELETE("/users/fcm-token")
    suspend fun disableNotifications(
        @Header("Authorization") accessToken: String,
        @Body token: FcmToken
    )

    @POST("/users/favorites/items")
    suspend fun addFavoriteItem(
        @Header("Authorization") accessToken: String,
        @Body item: FavoriteItem
    )

    @HTTP(method = "DELETE", path = "/users/favorites/items", hasBody = true)
    suspend fun deleteFavoriteItem(
        @Header("Authorization") accessToken: String,
        @Body item: FavoriteItem
    )

    @POST("/users/favorites/eateries")
    suspend fun addFavoriteEatery(
        @Header("Authorization") accessToken: String,
        @Body eatery: FavoriteEatery
    )

    @HTTP(method = "DELETE", path = "/users/favorites/eateries", hasBody = true)
    suspend fun deleteFavoriteEatery(
        @Header("Authorization") accessToken: String,
        @Body eatery: FavoriteEatery
    )

    @POST("/auth/get/authorize")
    suspend fun authorizeUser(
        @Header("Authorization") accessToken: String,
        @Body loginRequest: LoginRequest
    )

    @POST("/auth/get/refresh")
    suspend fun refreshAuthorizedUser(
        @Header("Authorization") accessToken: String,
        @Body loginPIN: LoginPIN
    ): SessionID

    @POST("/financials")
    suspend fun getFinancials(
        @Header("Authorization") accessToken: String,
        @Body sessionId: SessionID
    ): Financials

    @GET("/users/favorites/matches")
    suspend fun getFavoriteMatches(
        @Header("Authorization") accessToken: String,
    ): List<Match>
}