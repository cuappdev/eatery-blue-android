@file:Suppress("AddExplicitTargetToParameterAnnotation")

package com.cornellappdev.android.eatery.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "favorite_eateries") val favoriteEateries: List<Int> = emptyList(),
    @Json(name = "favorite_items") val favoriteItems: List<String> = emptyList(),
    @Json(name = "brb_balance") val brbBalance: Double = 0.0,
    @Json(name = "city_bucks_balance") val cityBucksBalance: Double = 0.0,
    @Json(name = "laundry_balance") val laundryBalance: Double = 0.0,
    @Json(name = "transactions") val transactions: List<Transaction>? = listOf(),
    @Json(name = "meal_swipes") val mealSwipes: Int? = null // todo - backend should make this
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "device_id") val deviceId: String = "",
    @Json(name = "fcm_token") val fcmToken: String = "",
    @Json(name = "pin") val pin: Int = 0
)

@JsonClass(generateAdapter = true)
data class AuthorizedUser(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "device_id") val deviceId: String = "",
    @Json(name = "fcm_token") val fcmToken: String = "",
    @Json(name = "pin") val pin: Int = 0
)

@JsonClass(generateAdapter = true)
data class Accounts(
    @Json(name = "brb") val brbBalance: Account? = null,
    @Json(name = "city_bucks") val cityBucksBalance: Account? = null,
    @Json(name = "laundry") val laundryBalance: Account? = null
)

@JsonClass(generateAdapter = true)
data class Account(
    @Json(name = "name") val name: String = "",
    @Json(name = "balance") val balance: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class Transactions(
    @Json(name = "transactions") val transactions: List<Transaction> = emptyList()
)

@JsonClass(generateAdapter = true)
data class Transaction(
    @Json(name = "amount") val amount: Double = 0.0,
    @Json(name = "accountName") val accountType: AccountType = AccountType.OTHER,
    @Json(name = "date") val date: String = "",
    @Json(name = "location") val location: String = "",
    @Json(name = "transactionType") val transactionType: TransactionType = TransactionType.NOOP // todo - backend should give this
)

/**
 * Categories for transactions used for filtering. More general than AccountType.
 */
enum class TransactionAccountType {
    MEAL_SWIPES,
    BRBS,
    CITY_BUCKS,
    LAUNDRY
}

/**
 * Specific account types as they show up in the backend.
 */
enum class AccountType {
    LAUNDRY,
    BRBS,
    CITY_BUCKS,
    OFF_CAMPUS,
    BEAR_TRADITIONAL,
    UNLIMITED,
    BEAR_BASIC,
    BEAR_CHOICE,
    HOUSE_MEAL_PLAN,
    HOUSE_AFFILIATE,
    FLEX,
    JUST_BUCKS,
    OTHER
    // todo - are there more?
}

fun AccountType.toTransactionAccountType(): TransactionAccountType {
    return when (this) {
        AccountType.BRBS -> TransactionAccountType.BRBS
        AccountType.CITY_BUCKS -> TransactionAccountType.CITY_BUCKS
        AccountType.LAUNDRY -> TransactionAccountType.LAUNDRY
        else -> TransactionAccountType.MEAL_SWIPES
    }
}

enum class TransactionType(val value: Int) {
    DEPOSIT(3), SPEND(1), NOOP(0), MISC(2);

    companion object {
        fun fromInt(value: Int) = TransactionType.entries.first { it.value == value }
    }
}
