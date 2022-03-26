package com.appdev.eateryblueandroid.ui.components.core.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.models.Eatery
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle

@Composable
fun RecentSearchList(
    eateries: List<Eatery>,
    selectEatery: (eatery: Eatery) -> Unit,
){
    var recentlySearchID = mutableListOf<Int>();
    var recentlySearchEateries = mutableListOf<Eatery>();
    recentlySearchID.add(1)
    eateries.forEach{

        if(recentlySearchID.contains(it.id)){
            recentlySearchEateries.add(it)
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(start = 16.dp),
    ){
        recentlySearchEateries.forEach{
            RecentSearchItem(it,selectEatery)
        }
    }
}
@Composable
fun RecentSearchItem(
    eatery: Eatery,
    selectEatery: (eatery: Eatery) -> Unit = {}
) {
    Surface(modifier = Modifier.clickable {
        selectEatery(eatery)
    }) {
        Row(
            verticalAlignment = Alignment.CenterVertically
//     modifier = Modifier.padding(start = 16.dp)
        ){
            Icon(
                painter = painterResource(id = R.drawable.ic_recent_eatery_icon),
                tint = colorResource(id = R.color.eateryBlue),
//         modifier = Modifier.size(15.dp , 15.dp),
                contentDescription = null
            )
            eatery.name?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(start = 9.dp),
                    textStyle = TextStyle.BODY_MEDIUM,
                    color = colorResource(id = R.color.eateryBlue),

                    )
            }

        }
    }

}