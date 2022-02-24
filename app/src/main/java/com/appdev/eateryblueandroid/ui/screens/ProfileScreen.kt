package com.appdev.eateryblueandroid.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.profile.AccountSummaries
import com.appdev.eateryblueandroid.ui.components.profile.TopBar
import com.appdev.eateryblueandroid.ui.components.profile.TransactionHistory

@Composable
fun ProfileScreen() {
    Column {
        TopBar()
        AccountSummaries()
        TransactionHistory()
    }
}