package com.cornellappdev.android.eateryblue.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "success") val success: Boolean,
    @Json(name = "data") val data: T? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class GetApiResponse<T>(
    @Json(name = "response") val response: T? = null,
    @Json(name = "exception") val exception: String? = null
)

@JsonClass(generateAdapter = true)
data class GetApiRequestBody<T>(
    val version: String,
    val method: String,
    val params: T
)

@JsonClass(generateAdapter = true)
data class GetApiUserParams(
    val sessionId: String
)

@JsonClass(generateAdapter = true)
data class GetApiAccountsParams(
    val sessionId: String,
    val userId: String
)

@JsonClass(generateAdapter = true)
data class GetApiTransactionHistoryParams(
    val paymentSystemType: Int,
    val sessionId: String,
    val queryCriteria: GetApiTransactionHistoryQueryCriteria
)

@JsonClass(generateAdapter = true)
data class GetApiTransactionHistoryQueryCriteria(
    val endDate: String,
    val institutionId: String,
    val maxReturn: Int,
    val startDate: String,
    val userId: String
)

@JsonClass(generateAdapter = true)
data class ReportSendBody(
    @Json(name = "eatery") val eatery: Int?,
    @Json(name = "content") val content: String
)
