package com.cornellappdev.android.eatery

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * This class is necessary to enable Hilt dependency injection. Can leave it empty as so.
 */
@HiltAndroidApp
class EateryBlueApplication : Application()
