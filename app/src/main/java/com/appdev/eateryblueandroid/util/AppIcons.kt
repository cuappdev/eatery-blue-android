package com.appdev.eateryblueandroid.util

import android.content.ComponentName
import android.content.pm.PackageManager
import com.appdev.eateryblueandroid.ui.appContext


enum class AppIcon {
    //App Icon Sheet
    DEFAULT, BLUE, RED, GREEN, YELLOW, ORANGE,

    //About page--subject to change
    ABOUT_SNOW, ABOUT_BLUE, ABOUT_ORIGINAL,
}

private val iconMap = hashMapOf(
    AppIcon.DEFAULT to "com.appdev.eateryblueandroid.ui.MainActivityDefault",
    AppIcon.BLUE to "com.appdev.eateryblueandroid.ui.MainActivityBlue",
    AppIcon.RED to "com.appdev.eateryblueandroid.ui.MainActivityRed",
    AppIcon.GREEN to "com.appdev.eateryblueandroid.ui.MainActivityGreen",
    AppIcon.YELLOW to "com.appdev.eateryblueandroid.ui.MainActivityYellow",
    AppIcon.ORANGE to "com.appdev.eateryblueandroid.ui.MainActivityOrange",

    )

/**
 * Changes the app's icon. Kills the app, and there doesn't seem to be any easy way around that :(
 */
fun changeIcon(icon: AppIcon) {
    //Disable all app icons
    for (key in iconMap.keys) {
        appContext.packageManager.setComponentEnabledSetting(
            ComponentName(appContext, iconMap[key]!!),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    //Enable this one
    appContext.packageManager.setComponentEnabledSetting(
        ComponentName(appContext, iconMap[icon]!!),
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP
    )
}

/**
 * Gets the AppIcon the app is currently set to use.
 */
fun currentIcon(): AppIcon {
    for (key in iconMap.keys) {
        if (appContext.packageManager.getComponentEnabledSetting(
                ComponentName(
                    appContext,
                    iconMap[key]!!
                )
            ) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        ) return key
    }
    return AppIcon.DEFAULT
}