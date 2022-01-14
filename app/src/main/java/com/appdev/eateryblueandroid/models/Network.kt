package com.appdev.eateryblueandroid.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name="success") val success: Boolean,
    @Json(name="data") val data: T? = null,
    @Json(name="error") val error: String? = null
)