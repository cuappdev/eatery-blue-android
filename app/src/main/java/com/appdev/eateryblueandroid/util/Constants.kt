package com.appdev.eateryblueandroid.util

import android.location.Location

object Constants {
    const val BACKEND_URL = "https://eatery-dev.cornellappdev.com/"
    const val GET_BACKEND_URL = "https://services.get.cbord.com/GETServices/services/json/"
    const val SESSIONID_WEBVIEW_URL = "https://get.cbord.com/cornell/full/login.php?mobileapp=1"
    const val SESSIONID_CU_LOGIN_SUBSTRING_IDENTIFIER = "shibidp"

    const val CORNELL_INSTITUTION_ID = "73116ae4-22ad-4c71-8ffd-11ba015407b1"
    const val FAVORITE_EATERIES_LOCAL_STORAGE = "favorite_eateries"

    var currentLocation: Location? = null
}