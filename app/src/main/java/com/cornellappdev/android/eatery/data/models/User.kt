@file:Suppress("AddExplicitTargetToParameterAnnotation")

package com.cornellappdev.android.eatery.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeviceId(
    @Json(name = "deviceUuid") val deviceId: String
)

@JsonClass(generateAdapter = true)
data class AuthTokens(
    @Json(name = "accessToken") val accessToken: String? = null,
    @Json(name = "refreshToken") val refreshToken: String? = null
)

@JsonClass(generateAdapter = true)
data class RefreshRequest(
    @Json(name = "deviceUuid") val deviceId: String,
    @Json(name = "refreshToken") val refreshToken: String
)

@JsonClass(generateAdapter = true)
data class FcmToken(
    @Json(name = "token") val fcmToken: String
)

@JsonClass(generateAdapter = true)
data class FavoriteItem(
    @Json(name = "name") val item: String
)

@JsonClass(generateAdapter = true)
data class FavoriteEatery(
    @Json(name = "eateryId") val eateryId: Int
)

@JsonClass(generateAdapter = true)
data class FavoritesResponse(
    @Json(name = "matches") val matches: List<Match>? = null
)

@JsonClass(generateAdapter = true)
data class Match(
    @Json(name = "eateryName") val eateryName: String? = null,
    @Json(name = "items") val items: List<Item>? = null
)

@JsonClass(generateAdapter = true)
data class Item(
    @Json(name = "name") val name: String? = null,
    @Json(name = "events") val events: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "favorite_eateries") val favoriteEateries: List<Int> = emptyList(),
    @Json(name = "favorite_items") val favoriteItems: List<String> = emptyList(),
    @Json(name = "brb_balance") val brbBalance: Double? = null,
    @Json(name = "city_bucks_balance") val cityBucksBalance: Double? = null,
    @Json(name = "laundry_balance") val laundryBalance: Double? = null,
    @Json(name = "transactions") val transactions: List<Transaction>? = emptyList(),
    @Json(name = "meal_swipes") val mealSwipes: Int? = null // todo - backend should make this
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "pin") val pin: String,
    @Json(name = "sessionId") val sessionId: String,
)

@JsonClass(generateAdapter = true)
data class LoginPIN(
    @Json(name = "pin") val pin: String
)

@JsonClass(generateAdapter = true)
data class SessionID(
    @Json(name = "sessionId") val sessionId: String? = null
)

@JsonClass(generateAdapter = true)
data class Financials(
    @Json(name = "accounts") val accounts: Accounts? = null,
    @Json(name = "transactions") val transactions: List<Transaction>? = null
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
data class Transaction(
    @Json(name = "amount") val amount: Double = 0.0,
    val tenderId: String? = "",
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
