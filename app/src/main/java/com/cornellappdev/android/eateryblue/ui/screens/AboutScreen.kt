package com.cornellappdev.android.eateryblue.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.eateryblue.R
import com.cornellappdev.android.eateryblue.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AboutScreen() {
    val uriCurrent = LocalUriHandler.current
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(top = 36.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "About Eatery",
                color = EateryBlue,
                style = EateryBlueTypography.h2,
                modifier = Modifier.padding(top = 7.dp)
            )
            Text(
                text = "Learn more about Cornell AppDev",
                color = GraySix,
                style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
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
                        tint = GrayFive,
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp)
                    )

                    Text(
                        text = "DESIGNED AND DEVELOPED BY",
                        style = EateryBlueTypography.subtitle1,
                        color = GrayFive,
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
                            style = EateryBlueTypography.h2,
                            color = Color.Black,
                        )
                        Text(
                            text = "AppDev",
                            style = EateryBlueTypography.h2,
                            color = Color.Black,
                        )
                    }
                }
            }
        }

        Column {
            TeamPosition.values().forEach {
                CreditsRow(position = it)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .then(Modifier.navigationBarsPadding())
                .height(48.dp)
                .fillMaxWidth(),
            onClick = {
                uriCurrent.openUri("https://www.cornellappdev.com/")
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = GrayOne,
                contentColor = Color.Black
            )
        ) {
            Icon(Icons.Default.Language, null)
            Spacer(
                Modifier.size(ButtonDefaults.IconSpacing)
            )
            Text(
                text = "Visit our website",
                style = EateryBlueTypography.h5,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
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
        "Gracie Jing",
        "William Ma",
        "Conner Swenberg",
        "TK Kong",
        "Connor Reinhold",
        "Sergio Diaz",
        "Matthew Wong"
    ),
    TeamPosition.IOS to listOf(
        "Reade Plunkett",
        "William Ma",
        "Justin Ngai",
        "Gonzalo Gonzalez",
        "Ethan Fine",
        "Daniel Vebman",
        "Sergio Diaz",
        "Jennifer Gu",
        "Antoinette Torres",
        "Jayson Hahn"
    ),
    TeamPosition.DESIGN to listOf(
        "Brendan Elliot",
        "Michael Huang",
        "Ravina Patel",
        "TK Kong",
        "Zain Khoja",
        "Gracie Jing",
        "Zixian Jia",
        "Kathleen Anderson"
    ),
    TeamPosition.BACKEND to listOf(
        "Orka Sinha",
        "Shungo Najima",
        "Yuna Shin",
        "Raahi Menon",
        "Alanna Zhou",
        "Conner Swenberg",
        "Archit Mehta",
        "Marya Kim",
        "Mateo Weiner",
        "Kidus Zegeye",
        "Thomas Vignos"
    ),
    TeamPosition.ANDROID to listOf(
        "Jonvi Rollins",
        "Aastha Shah",
        "Jonah Gershon",
        "Adam Kadhim",
        "Lesley Huang",
        "Connor Reinhold",
        "Jae Young Choi",
        "Yanlam Ko",
        "Chris Desir",
        "Kevin Sun",
        "Corwin Zhang",
        "Justin Guo",
        "Emily Hu",
        "Sophie Meng",
        "Gregor Guerrier"
    ),
    TeamPosition.MARKETER to listOf(
        "Neha Malepati",
        "Faith Earley",
        "Cat Zhang",
        "Lucy Zhang",
        "Jane Lee",
        "Vivian Park",
        "Carnell Zhou",
        "Matthew Wong"
    ),
)


@Composable
fun CreditsRow(position: TeamPosition) {
    // First item in the row is the position, e.g. Android Developer, Pod Leads.
    val lazyRowList = mutableListOf<Any>(position)
    val names: List<String> = teamRosterMap[position]!!
    names.forEach { name -> lazyRowList.add(name) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var scrolled by remember { mutableStateOf(false) }
    val alpha = animateFloatAsState(
        targetValue = if (scrolled) 1.0f else 0.0f,
        animationSpec = tween(250, 0, LinearEasing)
    )
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
            val scrollDist = ((screenWidth / 2 + (Math.random() * screenWidth)) * .65).toFloat()
            val multFactor = 50000
            coroutineScope.launch {
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
                val item = lazyRowList[it % lazyRowList.size]
                if (item is TeamPosition) {
                    Text(
                        text = teamNameMap[item]!!,
                        style = EateryBlueTypography.button,
                        color = Color.Black
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(17.dp))
                            .background(GrayOne)
                    ) {
                        Text(
                            text = item as String,
                            style = EateryBlueTypography.button,
                            color = Color.Black,
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
                    Icons.Filled.Star,
                    contentDescription = null,
                    tint = GrayOne,
                    modifier = Modifier
                        .height(7.dp)
                        .padding(start = 12.33.dp, end = 12.33.dp)
                        .width(7.33.dp)
                )
            })
    }
}
