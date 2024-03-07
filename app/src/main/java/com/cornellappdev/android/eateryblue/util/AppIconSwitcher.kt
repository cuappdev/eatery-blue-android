package com.cornellappdev.android.eateryblue.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.cornellappdev.android.eateryblue.R

enum class AppIcon {
    //App Icon Sheet
    DEFAULT, BLUE, RED, GREEN, YELLOW, ORANGE,
}

val iconMap = hashMapOf(
    AppIcon.DEFAULT to Pair(
        "com.cornellappdev.android.eateryblue.MainActivity",
        R.drawable.ic_default_off
    ),
    AppIcon.BLUE to Pair(
        "com.cornellappdev.android.eateryblue.MainActivityBlue",
        R.drawable.ic_blue_off
    ),
    AppIcon.RED to Pair(
        "com.cornellappdev.android.eateryblue.MainActivityRed",
        R.drawable.ic_red_off
    ),
    AppIcon.GREEN to Pair(
        "com.cornellappdev.android.eateryblue.MainActivityGreen",
        R.drawable.ic_green_off
    ),
    AppIcon.YELLOW to Pair(
        "com.cornellappdev.android.eateryblue.MainActivityYellow",
        R.drawable.ic_yellow_off
    ),
    AppIcon.ORANGE to Pair(
        "com.cornellappdev.android.eateryblue.MainActivityOrange",
        R.drawable.ic_orange_off
    )
)

/**
 * Changes the app's icon.
 */
fun changeIcon(context: Context, icon: AppIcon) {
    // Disable all other app icons
    for (key in iconMap.keys) {
        context.packageManager.setComponentEnabledSetting(
            ComponentName(context, iconMap[key]!!.first),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    // Enable the icon passed in
    context.packageManager.setComponentEnabledSetting(
        ComponentName(context, iconMap[icon]!!.first),
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP
    )
}

/**
 * Gets the AppIcon the app is currently set to use.
 */
fun currentIcon(context: Context): AppIcon {
    for (key in iconMap.keys) {
        if (context.packageManager.getComponentEnabledSetting(
                ComponentName(
                    context,
                    iconMap[key]!!.first
                )
            ) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        ) return key
    }
    return AppIcon.DEFAULT
}
