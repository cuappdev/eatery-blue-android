package com.cornellappdev.android.eateryblue.util

import com.cornellappdev.android.eateryblue.data.models.AccountType

object Constants {
    const val AVERAGE_WALK_SPEED = 1.42

    const val PASSWORD_ALIAS = "Eatery Alias"

    /*
    I have no idea if these strings actually correspond to the meal plans as they show up in the GET API. For example,
    You'd expect Bear Traditional to appear as "Bear Traditional" but it shows up as "Traditional Bear" ...?
    TODO: Find out what all the meal plans show up as, and substitute in the correct strings.
    */
    val mealPlanAccountMap: HashMap<String, AccountType> = hashMapOf(
        "off-campus value" to AccountType.OFF_CAMPUS,
        "traditional" to AccountType.BEAR_TRADITIONAL,
        "unlimited" to AccountType.UNLIMITED,
        "basic" to AccountType.BEAR_BASIC,
        "choice" to AccountType.BEAR_CHOICE,
        "house meal plan" to AccountType.HOUSE_MEALPLAN,
        "house affiliate" to AccountType.HOUSE_AFFILIATE,
        "flex" to AccountType.FLEX,
        "just bucks" to AccountType.JUST_BUCKS
    )
}
