package com.appdev.eateryblueandroid.ui.components.core.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextField
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.viewmodels.SearchViewModel
import com.appdev.eateryblueandroid.util.saveRecentSearch

//The typeable search bar the user can use to search up eateries
@Composable
fun TypeableSearchBar(
    searchViewModel: SearchViewModel
) {
    val typedText = searchViewModel.typedText.value
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            TextField(
                value = typedText,
                onValueChange = { searchViewModel.onTextChange(it) },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
                    .padding(12.dp),
                placeholder = "Search for grub...",
                backgroundColor = colorResource(id = R.color.gray00),
                focusRequester = focusRequester,
                onSubmit = { focusManager.clearFocus() },
                leftIcon = painterResource(id = R.drawable.ic_magnifying_glass),
            )
        }

        //If the text is blank then don't have a cancel button, else it will have a cancel button
        //to remove all text in the search bar
        if (typedText.isNotBlank()) {
            searchViewModel.transitionSearchWordsTyped()
            Text(
                text = "Cancel",
                textStyle = TextStyle.MISC_BACK,
                modifier = Modifier
                    .padding(start = 10.dp, top = 8.dp)
                    .clickable {
                        searchViewModel.onTextChange("")
                        focusManager.clearFocus()
                    }
            )
        }
    }
    //upon launch, request focus on keyboard to pull it up when screen is launched
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    //when you leave this screen, remove the text in the search bar
    DisposableEffect(key1 = searchViewModel) {
        onDispose {
            searchViewModel.onTextChange("")
        }
    }
}

