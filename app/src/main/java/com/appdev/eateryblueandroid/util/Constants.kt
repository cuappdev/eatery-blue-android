package com.appdev.eateryblueandroid.util

import android.location.Location

object Constants {
    const val BACKEND_URL = "https://eatery-dev.cornellappdev.com/"
    const val FAVORITE_EATERIES_LOCAL_STORAGE = "favorite_eateries"

    var currentLocation: Location? = null
}