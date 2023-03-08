package com.cornellappdev.android.eateryblue.ui.components.general

enum class Filter(val text: String) {
    UNDER_10("Under 10 min"),
    NORTH("North"),
    WEST("West"),
    CENTRAL("Central"),
    FAVORITES("Favorites"),
    BRB("BRBs"),
    CASH("Cash or credit"),
    SWIPES("Meal swipes");

    companion object {
        val PAYMENT_METHODS = setOf(BRB, CASH, SWIPES)
    }
}
