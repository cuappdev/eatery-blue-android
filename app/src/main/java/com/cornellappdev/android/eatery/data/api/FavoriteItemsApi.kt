package com.cornellappdev.android.eatery.data.api

interface FavoriteItemsApi {
    suspend fun favoriteItem(id: String)
    suspend fun removeFavoriteItem(id: String)
}