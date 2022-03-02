package com.appdev.eateryblueandroid.ui.components.profile

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.TextField

@Composable
fun TransactionHistorySearch(
    query: String,
    setQuery: (String) -> Unit
) {
    Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp)) {
        TextField(
            value = query,
            onValueChange = setQuery,
            placeholder = "Search for transactions...",
            backgroundColor = colorResource(id = R.color.gray00),
            leftIcon = painterResource(id = R.drawable.ic_search)
        )
    }
}