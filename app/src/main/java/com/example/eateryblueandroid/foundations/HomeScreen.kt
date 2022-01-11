package com.example.eateryblueandroid.foundations

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eateryblueandroid.R


@Composable
fun HomeScreen(){
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = colorResource(R.color.eateryBlue),
                modifier = Modifier.fillMaxHeight(0.13f)) {
                Column(
                ) {
                    Image(painter = painterResource(id = R.drawable.ic_eaterylogo), contentDescription = null)
                    Text(text = "EateryBlue", color = colorResource(id = R.color.white), fontSize = 34.sp)
                }
            }
        }
    ) {
        searchBar(text = "",
            onTextChanged = { onSearch("") },
            onCloseClicked = { onSearch("")},
            onSearchClicked = { onSearch("")} )

    }
}

@Composable
fun searchBar(text: String,
              onTextChanged: (String)-> Unit,
              onCloseClicked: (String) -> Unit,
              onSearchClicked: (String)-> Unit){

    Surface(
        modifier = Modifier
            .padding(10.dp, 20.dp, 10.dp, 20.dp)
            .fillMaxWidth()
            .fillMaxHeight(0.08f),
        color = colorResource(id = R.color.gray00),
        shape = RoundedCornerShape(8.dp)
    ){

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = {onTextChanged(it)},
            placeholder = {Text(
                text = "Search for grub here...",
                color = colorResource(id = R.color.gray05), fontSize = 12.sp)},
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(textColor = colorResource(id = R.color.gray05), backgroundColor = colorResource(
                id = R.color.gray00
            ), focusedIndicatorColor = colorResource(id = R.color.transparent), unfocusedIndicatorColor = colorResource(
                id = R.color.transparent
            )),
            leadingIcon = {Icon(painterResource(id = R.drawable.ic_search_icon), null)}
        )

    }
}

private fun onSearch(value: String){

}