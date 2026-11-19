package com.cornellappdev.android.eatery.data.models

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Basic identity info for the logged-in user, displayed on the Account page.
 * Any field may be null when the backend doesn't provide it.
 *
 * Held in memory only: the backend asks that profile data not be cached or logged.
 */
data class UserProfile(
    val name: String? = null,
    val email: String? = null,
    val photo: ImageBitmap? = null,
)

/** Response body of `POST /users/get-profile`. Every field may be null. */
@JsonClass(generateAdapter = true)
data class UserProfileResponse(
    @Json(name = "firstName") val firstName: String? = null,
    @Json(name = "middleName") val middleName: String? = null,
    @Json(name = "lastName") val lastName: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "photo") val photo: ProfilePhoto? = null,
)

/** A profile photo as base64-encoded image bytes of type [mimeType] (e.g. `image/jpeg`). */
@JsonClass(generateAdapter = true)
data class ProfilePhoto(
    @Json(name = "mimeType") val mimeType: String? = null,
    @Json(name = "data") val data: String? = null,
)

/**
 * Converts the backend response into a [UserProfile], joining the name parts and decoding
 * the photo. A photo that fails to decode is dropped rather than failing the whole profile.
 */
fun UserProfileResponse.toUserProfile(): UserProfile = UserProfile(
    name = listOf(firstName, middleName, lastName)
        .filterNot { it.isNullOrBlank() }
        .joinToString(" ")
        .ifEmpty { null },
    email = email?.takeIf { it.isNotBlank() },
    photo = photo?.toImageBitmap(),
)

private fun ProfilePhoto.toImageBitmap(): ImageBitmap? {
    if (mimeType?.startsWith("image/") == false) return null
    val bytes = runCatching { Base64.decode(data ?: return null, Base64.DEFAULT) }
        .getOrNull() ?: return null
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
}
