package com.appdev.eateryblueandroid.models

data class EaterySection(
    val name: String,
    val filter: (Eatery) -> Boolean
)