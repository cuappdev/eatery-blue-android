package com.appdev.eateryblueandroid.ui.components.core.search

import android.graphics.Color
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester

import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R

import com.appdev.eateryblueandroid.ui.components.core.*

import com.appdev.eateryblueandroid.ui.viewmodels.SearchViewModel


import androidx.compose.foundation.text.KeyboardOptions


import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color.Companion.Red


import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp


@Composable
fun TypeableSearchBar(
    searchViewModel: SearchViewModel,
    searchText : String) {

    val typedText = searchViewModel.typedText.value
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    Surface(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier.clickable {}.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
//                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
//            Row(
//                modifier = Modifier
//                    .background(colorResource(id = R.color.gray00))
//                    .padding(10.dp)
//                    .width(250.dp)
//
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_magnifying_glass),
//                    tint = colorResource(id = R.color.gray05),
//                    contentDescription = null,
//                    modifier = Modifier.padding(top = 3.dp)
//                )

                TextField(
                    value = typedText,
                    onValueChange = {newValue ->
                        searchViewModel.onTextChange(newValue)
                    },

                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(start = 5.dp),
                    placeholder = {
                        Text(
                            text = "Search for grub...",
                            color = colorResource(id = R.color.gray05),
                            textStyle = TextStyle.BODY_MEDIUM,
                    ) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    keyboardActions = KeyboardActions(onSearch = {
                        focusManager.clearFocus()
                    }),

                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = colorResource(id = R.color.gray00)
                    ),

                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_magnifying_glass),
                            tint = colorResource(id = R.color.gray05),
                            contentDescription = null,
                            modifier = Modifier.padding(top = 3.dp)
                        )
                    }
                )
//            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
//                Column(
//                    modifier = Modifier.weight(1f),
//                ) {
//                    TextField(
//                        value = filterText,
//                        onValueChange = { filterText = it },
//                        placeholder = "Search for grub...",
//                        backgroundColor = colorResource(id = R.color.gray00),
//                        focusRequester = focusRequester,
//                        onSubmit = { focusManager.clearFocus() },
//                        leftIcon = painterResource(id = R.drawable.ic_magnifying_glass)
//                    )
//                }
//            }
            if(typedText.isNotBlank()){
                Text(
                    text = "Cancel",
                    textStyle = TextStyle.BODY_MEDIUM,
                    color = colorResource(id = R.color.black),
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .align(Alignment.CenterVertically)
                        .clickable {  searchViewModel.onTextChange("")
                            focusManager.clearFocus()  }
                )
            }

        }

    }
}

//@Preview
//@Composable
//fun PreviewTypeableSearchBar(){
//    TypeableSearchBar("hello")
//}
