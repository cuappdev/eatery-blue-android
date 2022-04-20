package com.appdev.eateryblueandroid.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "success") val success: Boolean,
    @Json(name = "data") val data: T? = null,
    @Json(name = "error") val error: String? = null
)

data class GetApiResponse<T>(
    @Json(name = "response") val response: T? = null,
    @Json(name = "exception") val exception: String? = null
)

data class GetApiRequestBody<T>(
    val version: String,
    val method: String,
    val params: T
)

data class GetApiUserParams(
    val sessionId: String
)

data class GetApiAccountsParams(
    val sessionId: String,
    val userId: String
)

data class GetApiTransactionHistoryParams(
    val paymentSystemType: Int,
    val sessionId: String,
    val queryCriteria: GetApiTransactionHistoryQueryCriteria
)

data class GetApiTransactionHistoryQueryCriteria(
    val endDate: String,
    val institutionId: String,
    val maxReturn: Int,
    val startDate: String,
    val userId: String
)

data class ReportSendBody(
    @Json(name = "eatery_id") val eatery_id: Int,
    @Json(name = "type") val type: String,
    @Json(name = "content") val content: String
)