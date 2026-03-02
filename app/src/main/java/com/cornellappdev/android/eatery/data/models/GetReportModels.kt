package com.cornellappdev.android.eatery.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetApiResponse<T>(
    @Json(name = "response") val response: T? = null,
    @Json(name = "exception") val exception: String? = null
)

@JsonClass(generateAdapter = true)
data class ReportSendBody(
    @Json(name = "eatery") val eatery: Int?,
    @Json(name = "content") val content: String
)