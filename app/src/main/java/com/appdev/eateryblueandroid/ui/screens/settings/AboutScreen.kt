package com.appdev.eateryblueandroid.ui.screens.settings

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun AboutScreen(profileViewModel: ProfileViewModel) {
    fun onBack() {
        profileViewModel.transitionSettings()
    }
    val uriCurrent = LocalUriHandler.current
    val interactionSource = MutableInteractionSource()
    Column (modifier = Modifier.verticalScroll(rememberScrollState())) {
        Column(
            modifier = Modifier
                .padding(top = 36.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, bottom = 5.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_leftarrow),
                    contentDescription = null,
                    tint = colorResource(id = R.color.black),
                    modifier = Modifier
                        .clickable(
                            onClick = { onBack() },
                            interactionSource = interactionSource,
                            indication = null
                        )
                        .clip(CircleShape)

                )
            }

            Text(
                text = "About Eatery",
                color = colorResource(id = R.color.eateryBlue),
                textStyle = TextStyle.HEADER_H1,
                modifier = Modifier.padding(top = 7.dp)
            )
            Text(
                text = "Learn more about Cornell AppDev",
                textStyle = TextStyle.APPDEV_BODY_MEDIUM,
                color = colorResource(id = R.color.gray06),
                modifier = Modifier.padding(top = 7.dp, bottom = 24.dp)
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_appdev),
                        contentDescription = null,
                        tint = colorResource(id = R.color.gray05),
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp)
                    )

                    Text(
                        text = "DESIGNED AND DEVELOPED BY",
                        textStyle = TextStyle.LABEL_MEDIUM,
                        color = colorResource(id = R.color.gray05),
                        modifier = Modifier.padding(top = 12.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    ) {
                        Text(
                            text = "Cornell",
                            textStyle = TextStyle.HEADER_H1_NORMAL,
                            color = colorResource(id = R.color.black),
                        )
                        Text(
                            text = "AppDev",
                            textStyle = TextStyle.HEADER_H1,
                            color = colorResource(id = R.color.black),
                        )
                    }
                }
            }
        }
        //TOP PART END

        Column {
            TeamPosition.values().forEach {
                CreditsRow(position = it)
            }

        }

        Button(
            shape = RoundedCornerShape(corner = CornerSize(24.dp)),
            modifier = Modifier
                .padding(top = 70.dp, start = 16.dp, end = 16.dp, bottom = 60.dp)
                .height(48.dp)
                .fillMaxWidth(),
            onClick = {
                uriCurrent.openUri("https://www.cornellappdev.com/")
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(id = R.color.gray01),
                contentColor = colorResource(id = R.color.black)
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically)
            {
                Icon(painter = painterResource(id = R.drawable.ic_globe), "")
                Text(
                    text = "Visit our website",
                    textStyle = TextStyle.HEADER_H4,
                    color = colorResource(id = R.color.black),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

    }

    BackHandler {
        onBack()
    }
}

enum class TeamPosition {
    POD_LEAD, IOS, DESIGN, BACKEND, ANDROID, MARKETER
}

private val teamNameMap = hashMapOf(
    TeamPosition.POD_LEAD to "Pod Leads",
    TeamPosition.IOS to "iOS Developers",
    TeamPosition.DESIGN to "Product Designers",
    TeamPosition.BACKEND to "Backend Developers",
    TeamPosition.ANDROID to "Android Developers",
    TeamPosition.MARKETER to "Marketers",
)

private val teamRosterMap: HashMap<TeamPosition, List<String>> = hashMapOf(
    TeamPosition.POD_LEAD to listOf(
        "Sergio Diaz",
        "Lia Chandra",
        "Hanzheng Li",
        "Haichen Wang",
        "Noah Solomon"
    ),
    TeamPosition.IOS to listOf(
        "Amy Huang",
        "Noah Pikielny",
        "Justin Ngai",
        "Mathew Scullin",
        "Reade Plunkett",
        "Vivian Nguyen",
        "Elvis Marcelo",
        "Sylvan Martin"
    ),
    TeamPosition.DESIGN to listOf(
        "Amanda He",
        "Tise Alatise",
        "Emily Romero",
        "Gracie Jing",
        "Ravina Patel",
        "Christina Zeng",
        "Zixian Jia",
        "Michelle Dong",
        "Jin Shang",
        "Kayla Sprayberry",
        "Liam Du",
        "Cindy Huang"
    ),
    TeamPosition.BACKEND to listOf(
        "Gonzalo Gonzalez",
        "Shungo Najima",
        "Kate Liang",
        "Joyce Wu",
        "Jessica Sylvester",
        "Kidus Zegeye",
        "Alanna Zhou",
        "Mateo Weiner",
        "Archit Mehta",
        "Marya Kim"
    ),
    TeamPosition.ANDROID to listOf(
        "Aastha Shah",
        "Chris Desir",
        "Junyu Wang",
        "Kevin Sun",
        "Shiyuan Huang",
        "Justin Jiang",
        "Aarushi Singh",
        "Corwin Zhang",
        "Benjamin Harris",
        "Jonah Gershon",
        "Emily Hu",
        "Maxwell Pang",
        "Justin Guo"
    ),
    TeamPosition.MARKETER to listOf(
        "Gordon Tran",
        "Chloe Kanders",
        "Neha Malepati",
        "Jonna Chen",
        "Vivian Park",
        "Carnell Zhou",
        "Lucy Zhang",
        "Jane Lee",
        "Matthew Wong"
    ),
)

private class RowItem(val value: Any)

@Composable
fun CreditsRow(position: TeamPosition) {
    val lazyRowList = mutableListOf(RowItem(position))
    val names: List<String> = teamRosterMap[position]!!
    names.forEach { lazyRowList.add(RowItem(it)) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var scrolled by remember {mutableStateOf(false)}
    val alpha = animateFloatAsState(targetValue = if (scrolled) 1.0f else 0.0f, animationSpec = tween(250, 0, LinearEasing))

    val configuration = LocalConfiguration.current
    val screenDensity = configuration.densityDpi / 160f

    LazyRow(
        modifier = Modifier
            .padding(top = 12.dp, bottom = 12.dp)
            .fillMaxWidth()
            .alpha(alpha.value),
        state = listState,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!scrolled) {
            val screenWidth = configuration.screenWidthDp.toFloat() * screenDensity
            val scrollDist = screenWidth/2 + (Math.random()*screenWidth).toFloat()
            val multFactor = 50000
            coroutineScope.launch {
                //listState.scrollToItem(index = lazyRowList.size * 500,0)
                listState.animateScrollBy(5000.0f, snap(0))
                scrolled = true
                listState.animateScrollBy(
                    scrollDist * multFactor,
                    tween(5000 * multFactor, 0, LinearEasing)
                )

            }
        }
        items(
            count = Int.MAX_VALUE,
            itemContent = {
                val index = (it) % lazyRowList.size
                if (lazyRowList[index].value is TeamPosition) {
                    Text(
                        text = teamNameMap[lazyRowList[index].value]!!,
                        textStyle = TextStyle.BODY_SEMIBOLD,
                        color = colorResource(id = R.color.black)
                    )
                }
                else {
                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(17.dp))
                            .background(
                                colorResource(id = R.color.gray01)
                            )
                    ) {
                        Text(
                            text = lazyRowList[index].value as String,
                            textStyle = TextStyle.BODY_SEMIBOLD,
                            color = colorResource(id = R.color.black),
                            modifier = Modifier.padding(
                                start = 10.dp,
                                top = 8.dp,
                                bottom = 8.dp,
                                end = 10.dp
                            ),
                            maxLines = 1
                        )
                    }
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_star),
                    contentDescription = null,
                    tint = colorResource(id = R.color.gray01),
                    modifier = Modifier
                        .height(7.dp)
                        .padding(start = 12.33.dp, end = 12.33.dp)
                        .width(7.33.dp)
                )


            }
        )
    }
    /*
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 12.dp, bottom = 12.dp)
            .horizontalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        Text(
            text = teamNameMap[position]!!,
            textStyle = TextStyle.BODY_SEMIBOLD,
            color = colorResource(id = R.color.black),
            modifier = Modifier.padding(start = 12.33.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_star),
            contentDescription = null,
            tint = colorResource(id = R.color.gray01),
            modifier = Modifier
                .height(7.dp)
                .padding(start = 12.33.dp, end = 12.33.dp)
                .width(7.33.dp)
        )

        val names: List<String> = teamRosterMap[position]!!
        names.forEach { name ->
            Box(
                modifier = Modifier
                    .height(34.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .background(
                        colorResource(id = R.color.gray01)
                    )
            ) {
                Text(
                    text = name,
                    textStyle = TextStyle.BODY_SEMIBOLD,
                    color = colorResource(id = R.color.black),
                    modifier = Modifier.padding(
                        start = 10.dp,
                        top = 8.dp,
                        bottom = 8.dp,
                        end = 10.dp
                    ),
                    maxLines = 1
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_star),
                contentDescription = null,
                tint = colorResource(id = R.color.gray01),
                modifier = Modifier
                    .height(7.dp)
                    .padding(start = 12.33.dp, end = 12.33.dp)
                    .width(7.33.dp)
            )
        }
    }*/
}

