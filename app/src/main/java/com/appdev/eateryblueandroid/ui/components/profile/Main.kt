package com.appdev.eateryblueandroid.ui.components.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.models.*
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.util.formatLocation
import com.appdev.eateryblueandroid.util.removeSpecialCharacters

@Composable
fun Main(
    user: User,
    accountFilter: AccountType,
    transactionQuery: String,
    updateQuery: (updatedQuery: String) -> Unit,
    showBottomSheet: () -> Unit
) {
    var displayQuery by remember { mutableStateOf(transactionQuery) }
    val transactionHistoryList = user.transactions?.filter{
        it.transactionType == TransactionType.SPEND && it.accountType == accountFilter
                && removeSpecialCharacters(formatLocation(it.location)).contains(
            removeSpecialCharacters(transactionQuery)
        )
    }?.map { ProfileItem.TransactionHistoryItem(it) } ?: listOf()

    val profileItems: List<ProfileItem> = listOf(
        listOf(ProfileItem.AccountSummary),
        listOf(ProfileItem.SectionSeparator),
        listOf(ProfileItem.TransactionHistoryHeader),
        listOf(ProfileItem.TransactionHistorySearch),
        transactionHistoryList,
        if(transactionHistoryList.isEmpty()) listOf(ProfileItem.NoTransactions) else listOf()
    ).flatten()

    LazyColumn(
        contentPadding = PaddingValues(bottom=100.dp)
    ) {
        items(profileItems) { item ->
            when(item) {
                is ProfileItem.AccountSummary ->
                    AccountSummaries(user.accounts)
                is ProfileItem.SectionSeparator ->
                    SectionSeparator()
                is ProfileItem.TransactionHistoryHeader ->
                    TransactionHistoryHeader(
                        account = accountFilter,
                        showAccountSelector = showBottomSheet
                    )
                is ProfileItem.TransactionHistorySearch ->
                    TransactionHistorySearch(
                        query = displayQuery,
                        setQuery = {
                            displayQuery = it
                            updateQuery(it)
                        }
                    )
                is ProfileItem.TransactionHistoryItem ->
                    TransactionHistory(item.transaction)
                is ProfileItem.NoTransactions ->
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text("No transactions to display", textStyle = TextStyle.HEADER_H6)
                    }
            }
        }
    }
}

sealed class ProfileItem {
    object AccountSummary: ProfileItem()
    object SectionSeparator: ProfileItem()
    object TransactionHistoryHeader: ProfileItem()
    object TransactionHistorySearch: ProfileItem()
    data class TransactionHistoryItem(val transaction: Transaction): ProfileItem()
    object NoTransactions: ProfileItem()
}