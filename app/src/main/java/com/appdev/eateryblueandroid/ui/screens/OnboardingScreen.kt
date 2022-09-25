package com.appdev.eateryblueandroid.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.components.login.ErrorSection
import com.appdev.eateryblueandroid.ui.components.login.LoginButton
import com.appdev.eateryblueandroid.ui.components.login.TextInputs
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel
import com.appdev.eateryblueandroid.util.ColorType
import com.appdev.eateryblueandroid.util.OnboardingRepository
import com.appdev.eateryblueandroid.util.overrideStatusBarColor
import com.google.accompanist.pager.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun OnboardingScreen(
    profileViewModel: ProfileViewModel
) {
    val interactionSource = MutableInteractionSource()
    var stage: Int by remember {
        mutableStateOf(0)
    }

    val goBackToMain = {
        stage = 0
    }

    val alpha = animateFloatAsState(
        targetValue = if (stage == 0 || stage == 2) 1f else 0f
    )

    Box {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .alpha(alpha.value),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight(.9f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_eaterylogo_blue),
                    tint = colorResource(R.color.eateryBlue),
                    modifier = Modifier
                        .width(96.dp)
                        .height(96.dp),
                    contentDescription = null
                )
                Text(
                    text = "Eatery",
                    color = colorResource(id = R.color.eateryBlue),
                    textStyle = TextStyle.SUPER_TITLE
                )

                // Get Started Button
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 48.dp, end = 48.dp),
                    elevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .background(colorResource(id = R.color.white))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = rememberRipple()
                            ) {
                                stage = 1

                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            textStyle = TextStyle.HEADER_H4,
                            text = "Get Started",
                            modifier = Modifier.padding(top = 13.5.dp, bottom = 13.5.dp)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_appdev),
                    modifier = Modifier.padding(end = 6.dp),
                    contentDescription = null,
                    tint = colorResource(id = R.color.gray03)
                )
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = colorResource(R.color.gray03),
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append("Cornell")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = colorResource(R.color.gray03),
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("AppDev")
                        }
                    }
                )
            }
        }

        if (stage == 1 || stage == 3)
            OnboardingViewPager(
                Modifier.alpha(1 - alpha.value),
                goBackToMain = goBackToMain,
                profileViewModel = profileViewModel
            )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun OnboardingViewPager(
    modifier: Modifier = Modifier,
    goBackToMain: () -> Unit,
    profileViewModel: ProfileViewModel
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        HorizontalPager(
            count = 4,
            modifier = Modifier
                .then(modifier)
                .fillMaxHeight(),
            state = pagerState,
            contentPadding = PaddingValues(horizontal = (1).dp)
        ) { page ->
            when (page) {
                0 -> {
                    OnboardingPage(
                        num = 0,
                        goBackToMain = goBackToMain,
                        pagerState = pagerState,
                        coroutineScope = coroutineScope,
                        pagerOffset = calculateCurrentOffsetForPage(0),
                        icons = listOf(
                            IconData(
                                painter = painterResource(id = R.drawable.ic_place),
                                offsetX = (-150).dp,
                                offsetY = (-190).dp,
                                rotate = -12f
                            ),
                            IconData(
                                painter = painterResource(id = R.drawable.ic_brbs),
                                offsetX = (-165).dp,
                                offsetY = (120).dp,
                                rotate = -24f
                            ),
                            IconData(
                                painter = painterResource(id = R.drawable.ic_item),
                                offsetX = 160.dp,
                                offsetY = 0.dp,
                                rotate = 0f
                            )
                        )
                    )
                }
                1 -> {
                    OnboardingPage(
                        num = 1,
                        goBackToMain = goBackToMain,
                        pagerState = pagerState,
                        coroutineScope = coroutineScope,
                        pagerOffset = calculateCurrentOffsetForPage(1),
                        icons = listOf(
                            IconData(
                                painter = painterResource(id = R.drawable.ic_selected_off),
                                offsetX = (-150).dp,
                                offsetY = (-190).dp,
                                rotate = -12f
                            ),
                            IconData(
                                painter = painterResource(id = R.drawable.ic_bell),
                                offsetX = 160.dp,
                                offsetY = 120.dp,
                                rotate = 0f
                            )
                        )
                    )
                }
                2 -> {
                    OnboardingPage(
                        num = 2,
                        goBackToMain = goBackToMain,
                        pagerState = pagerState,
                        coroutineScope = coroutineScope,
                        pagerOffset = calculateCurrentOffsetForPage(2),
                        icons = listOf(
                            IconData(
                                painter = painterResource(id = R.drawable.ic_clock),
                                offsetX = (-140).dp,
                                offsetY = (40).dp,
                                rotate = -12f
                            ),
                            IconData(
                                painter = painterResource(id = R.drawable.ic_watch),
                                offsetX = 130.dp,
                                offsetY = (-20).dp,
                                rotate = 24f,
                                scale = 8f
                            )
                        )
                    )
                }
                3 -> {
                    LoginPage(
                        coroutineScope = coroutineScope,
                        pagerState = pagerState,
                        profileViewModel = profileViewModel
                    )
                }
            }
        }
    }
}

private data class IconData(
    val painter: Painter,
    val offsetX : Dp,
    val offsetY : Dp,
    val rotate : Float,
    val scale : Float = 4f
)




@OptIn(ExperimentalPagerApi::class)
@Composable
private fun OnboardingPage(
    num: Int,
    pagerState: PagerState,
    goBackToMain: () -> Unit,
    coroutineScope: CoroutineScope,
    pagerOffset: Float,
    icons : List<IconData> = listOf()
) {
    val interactionSource = MutableInteractionSource()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        // Top Bar & Text
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, start = 21.dp, bottom = 12.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_leftarrow),
                    contentDescription = null,
                    tint = colorResource(id = R.color.black),
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                when (num) {
                                    0 -> {
                                        goBackToMain()
                                    }
                                    1 -> {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(0)
                                        }
                                    }
                                    2 -> {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(1)
                                        }
                                    }
                                }
                            },
                            interactionSource = interactionSource,
                            indication = null
                        )
                        .clip(CircleShape)
                )
            }
            Text(
                text = when (num) {
                    0 -> "Upcoming Menus"
                    1 -> "Favorites"
                    else -> "Wait Times"
                },
                textStyle = TextStyle.HEADER_H2,
                color = colorResource(id = R.color.eateryBlue),
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = when (num) {
                    0 -> "See menus by date and plan ahead"
                    1 -> "Save and quickly find eateries and items"
                    else -> "Check for crowds in real time to avoid lines"
                },
                textStyle = TextStyle.APPDEV_BODY_MEDIUM,
                color = colorResource(id = R.color.gray06),
                modifier = Modifier.padding(top = 7.dp, start = 16.dp)
            )
        }

        // Phone
        //TODO: Implement effects from [calculateCurrentOffsetForPage]
        Box(modifier = Modifier.fillMaxWidth()) {
            icons.forEach {
                Icon(
                    painter = it.painter, contentDescription = null,
                    tint = colorResource(id = R.color.blue_light),
                    modifier = Modifier
                        .offset(it.offsetX, it.offsetY)
                        .scale(it.scale)
                        .align(Alignment.Center)
                        .rotate(it.rotate)
                )
            }

            Row(
                modifier = Modifier
                    // This is bad but is the only way I could get this to be sized correctly.
                    .height(LocalConfiguration.current.screenHeightDp.dp - 204.dp)
                    .graphicsLayer {
                        val pageOffset =
                            if (num == 2) pagerOffset.coerceIn(-1.0f, 0f) else pagerOffset

                        val lerp = { startValue: Float, endValue: Float, fraction: Float ->
                            startValue + (fraction * (endValue - startValue))
                        }

                        //scaleX = 1f - pagerOffset.coerceIn(-1.0f, 1f).absoluteValue / 10f;
                        //scaleY = 1f - pagerOffset.coerceIn(-1.0f, 1f).absoluteValue / 10f;

                        val offsetLerp = lerp(
                            0f,
                            240f,
                            pageOffset
                        )

                        translationX = offsetLerp
                    },
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.active_mock_0),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Next Button
        Surface(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
                .height(48.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(colorResource(id = R.color.gray00))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = rememberRipple()
                    ) {
                        when (num) {
                            0 -> {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            }
                            1 -> {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(2)
                                }
                            }
                            2 -> {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(3)
                                }
                            }
                        }
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    textStyle = TextStyle.HEADER_H4,
                    text = "Next"
                )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun LoginPage(
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    profileViewModel: ProfileViewModel
) {
    val interactionSource = MutableInteractionSource()
    var netid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val passwordFocus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val state = profileViewModel.state.collectAsState()
    val eateryBlue = colorResource(R.color.eateryBlue)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(start = 0.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, end = 16.dp, bottom = 12.dp, start = 21.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_leftarrow),
                    contentDescription = null,
                    tint = colorResource(id = R.color.black),
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(2)
                                }

                            },
                            interactionSource = interactionSource,
                            indication = null
                        )
                        .clip(CircleShape)
                )
                Text(
                    text = "Skip",
                    color = colorResource(R.color.black),
                    textStyle = TextStyle.HEADER_H4,
                    modifier = Modifier.clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) {
                        OnboardingRepository.saveOnboardingInfo(true)
                        overrideStatusBarColor(eateryBlue, ColorType.INTERP)
                    }
                )
            }
            Text(
                text = "Log in with Eatery",
                textStyle = TextStyle.HEADER_H2,
                color = colorResource(id = R.color.eateryBlue),
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = "See your meal swipes, BRBs, and more",
                textStyle = TextStyle.APPDEV_BODY_MEDIUM,
                color = colorResource(id = R.color.gray06),
                modifier = Modifier.padding(top = 7.dp, start = 16.dp)
            )

            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            ) {
                TextInputs(
                    netid = netid,
                    password = password,
                    setNetId = { netid = it },
                    setPassword = { password = it },
                    login = { profileViewModel.initiateLogin(netid, password) },
                    passwordFocus = passwordFocus,
                    hideKeyboard = { focusManager.clearFocus() }
                )

                state.value.let { it ->
                    if (it is ProfileViewModel.State.LoginFailure) {
                        ErrorSection(it.error)
                    }
                }

                LoginButton(
                    profileViewModel = profileViewModel,
                    login = {
                        if (netid.isNotEmpty() && password.isNotEmpty()) {
                            focusManager.clearFocus()
                            profileViewModel.initiateLogin(netid, password)
                        }
                    },
                    clickable = netid.isNotEmpty() && password.isNotEmpty()
                )
            }
        }
    }
}