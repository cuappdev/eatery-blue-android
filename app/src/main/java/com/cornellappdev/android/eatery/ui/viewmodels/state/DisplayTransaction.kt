package com.cornellappdev.android.eatery.ui.viewmodels.state

import com.cornellappdev.android.eatery.data.models.TransactionAccountType

data class DisplayTransaction(
    val id: String,
    val amount: Double,
    val accountType: TransactionAccountType,
    val date: String,
    val location: String,
    val formattedDate: String
)

