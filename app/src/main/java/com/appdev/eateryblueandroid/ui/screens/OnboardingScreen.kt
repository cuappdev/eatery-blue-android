package com.appdev.eateryblueandroid.ui.screens

import android.icu.math.BigDecimal
import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.appdev.eateryblueandroid.R
import com.appdev.eateryblueandroid.ui.components.core.Text
import com.appdev.eateryblueandroid.ui.components.core.TextStyle
import com.appdev.eateryblueandroid.ui.components.login.ErrorSection
import com.appdev.eateryblueandroid.ui.components.login.LoginButton
import com.appdev.eateryblueandroid.ui.components.login.LoginTextInputs
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel
import com.appdev.eateryblueandroid.util.ColorType
import com.appdev.eateryblueandroid.util.Constants.eateryBlueColor
import com.appdev.eateryblueandroid.util.OnboardingRepository
import com.appdev.eateryblueandroid.util.overrideStatusBarColor
import com.google.accompanist.pager.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.pow

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
    val firstPagerState = rememberPagerState()
    val secondPagerState = rememberPagerState()
    val thirdPagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val coroutineScopeHelper = rememberCoroutineScope()

    val scrollingFollowingTriple by remember {
        derivedStateOf {
            Triple(secondPagerState, firstPagerState, thirdPagerState)
        }
    }

    Log.d("PagerState", scrollingFollowingTriple.toString())

    coroutineScopeHelper.launch {
        Log.d(
            "PagerState",
            "Init"
        )
        val (scrollingState, followingState, followingState2) = scrollingFollowingTriple
        val pagePart: State<Float> =
            mutableStateOf(scrollingState.currentPage + scrollingState.currentPageOffset)
        val divideAndRemainder: Array<BigDecimal> = arrayOf(
            BigDecimal.valueOf(pagePart.value.toDouble())
                .divide(BigDecimal.ONE),
            BigDecimal.valueOf(pagePart.value.toDouble())
                .remainder(BigDecimal.ONE)
        )

        Log.d(
            "PagerState",
            "Scrolled with " + divideAndRemainder[0] + " and " + divideAndRemainder[1]
        )

        followingState.scrollToPage(
            divideAndRemainder[0].toInt(),
            divideAndRemainder[1].toFloat(),
        )
        followingState2.scrollToPage(
            divideAndRemainder[0].toInt(),
            divideAndRemainder[1].toFloat()
        )
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .then(modifier),
        verticalArrangement = Arrangement.Top
    ) {
        HorizontalPager(
            count = 4,
            state = firstPagerState,
            userScrollEnabled = false
        ) { page ->
            OnboardingHeader(
                num = page,
                pagerState = secondPagerState,
                goBackToMain = goBackToMain,
                pagerOffset = calculateCurrentOffsetForPage(page)
            )
        }

        HorizontalPager(
            count = 4,
            modifier = Modifier
                .then(modifier)
                .padding(top = 16.dp).zIndex(-1f),
            state = secondPagerState,
            contentPadding = PaddingValues(horizontal = (50).dp)
        ) { page ->
            when (page) {
                0 -> {
                    OnboardingPage(
                        num = 0,
                        goBackToMain = goBackToMain,
                        pagerState = secondPagerState,
                        coroutineScope = coroutineScope,
                        pagerOffset = calculateCurrentOffsetForPage(0),
                        icons = listOf(
                            IconData(
                                painter = painterResource(id = R.drawable.ic_place),
                                offsetX = -.6f,
                                offsetY = -.33f,
                                rotate = -12f
                            ),
                            IconData(
                                painter = painterResource(id = R.drawable.ic_brbs),
                                offsetX = -.62f,
                                offsetY = .35f,
                                rotate = -24f
                            ),
                            IconData(
                                painter = painterResource(id = R.drawable.ic_item),
                                offsetX = .6f,
                                offsetY = .1f,
                                rotate = 0f
                            )
                        )
                    )
                }
                1 -> {
                    OnboardingPage(
                        num = 1,
                        goBackToMain = goBackToMain,
                        pagerState = secondPagerState,
                        coroutineScope = coroutineScope,
                        pagerOffset = calculateCurrentOffsetForPage(1),
                        icons = listOf(
                            IconData(
                                painter = painterResource(id = R.drawable.ic_selected_off),
                                offsetX = -.55f,
                                offsetY = -.35f,
                                rotate = -12f
                            ),
                            IconData(
                                painter = painterResource(id = R.drawable.ic_bell),
                                offsetX = .6f,
                                offsetY = .28f,
                                rotate = 0f
                            )
                        )
                    )
                }
                2 -> {
                    OnboardingPage(
                        num = 2,
                        goBackToMain = goBackToMain,
                        pagerState = secondPagerState,
                        coroutineScope = coroutineScope,
                        pagerOffset = calculateCurrentOffsetForPage(2),
                        icons = listOf(
                            IconData(
                                painter = painterResource(id = R.drawable.ic_clock),
                                offsetX = -.52f,
                                offsetY = .2f,
                                rotate = -12f
                            ),
                            IconData(
                                painter = painterResource(id = R.drawable.ic_watch_big),
                                offsetX = .55f,
                                offsetY = -.15f,
                                rotate = 24f
                            )
                        )
                    )
                }
                3 -> {
                    LoginPage(
                        coroutineScope = coroutineScope,
                        pagerState = secondPagerState,
                        profileViewModel = profileViewModel,
                        pagerOffset = calculateCurrentOffsetForPage(3)
                    )
                }
            }
        }

        HorizontalPager(
            count = 4,
            state = thirdPagerState,
            userScrollEnabled = false,
            modifier = Modifier.then(modifier)
        ) { page ->
            if (page < 3)
                OnboardingNextButton(
                    num = page,
                    pagerState = secondPagerState,
                    pagerOffset = calculateCurrentOffsetForPage(page)
                )
        }
    }
}

private data class IconData(
    val painter: Painter,
    val offsetX: Float,
    val offsetY: Float,
    val rotate: Float
)


@OptIn(ExperimentalPagerApi::class)
@Composable
private fun OnboardingPage(
    num: Int,
    pagerState: PagerState,
    goBackToMain: () -> Unit,
    coroutineScope: CoroutineScope,
    pagerOffset: Float,
    icons: List<IconData> = listOf()
) {
    val interactionSource = MutableInteractionSource()

    val phoneY : Dp = LocalConfiguration.current.screenHeightDp.dp - 204.dp
    val phoneX = (.493f * phoneY.value).dp

    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Box (modifier = Modifier.fillMaxWidth()){
            icons.forEach {
                Icon(
                    painter = it.painter,
                    contentDescription = null,
                    modifier = Modifier
                        .offset(phoneX * it.offsetX, phoneY * it.offsetY)
                        .width(96.dp)
                        .height(96.dp)
                        .align(Alignment.Center)
                        .rotate(it.rotate).graphicsLayer {
                            val pageOffset = -pagerOffset.coerceIn(-1f, 1f)

                            val lerp = { startValue: Float, endValue: Float, fraction: Float ->
                                startValue + (fraction * (endValue - startValue))
                            }

                            scaleX = 1f - pagerOffset.coerceIn(-1.0f, 1f).absoluteValue
                            scaleY = 1f - pagerOffset.coerceIn(-1.0f, 1f).absoluteValue

                            alpha = 1f - pagerOffset.coerceIn(-1.0f, 1f).absoluteValue

                            val offsetLerp = lerp(
                                0f,
                                240f,
                                pageOffset
                            )

                            translationX = offsetLerp
                        },
                    tint = colorResource(R.color.blue_light)
                )
            }
            // Phone
            Row(
                modifier = Modifier
                    // This is bad but is the only way I could get this to be sized correctly.
                    .height(phoneY)
                    .graphicsLayer {
                        val pageOffset =
                            if (num < 2) 0f else -pagerOffset.coerceIn(0f, 1f)

                        val lerp = { startValue: Float, endValue: Float, fraction: Float ->
                            startValue + (fraction * (endValue - startValue))
                        }

                        scaleX = 1f - pagerOffset.coerceIn(-1.0f, 1f).absoluteValue / 10f;
                        scaleY = 1f - pagerOffset.coerceIn(-1.0f, 1f).absoluteValue / 10f;

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
                    painter = painterResource(id = when(num) {
                        0 -> R.drawable.active_mock_0_4x
                        1 -> R.drawable.active_mock_1_4x
                        else -> R.drawable.active_mock_2_4x
                    }),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
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
    profileViewModel: ProfileViewModel,
    pagerOffset: Float
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
            // This is bad but required since HorizontalPager is buggy and quite limited.
            .requiredWidth(LocalConfiguration.current.screenWidthDp.dp - 32.dp)
            .height(LocalConfiguration.current.screenHeightDp.dp - 204.dp)
            .graphicsLayer {
                val pageOffset =
                    -pagerOffset.coerceIn(-1f, 0f)

                val lerp = { startValue: Float, endValue: Float, fraction: Float ->
                    startValue + (fraction * (endValue - startValue))
                }

                val offsetLerp = lerp(
                    0f,
                    240f,
                    pageOffset
                )

                translationX = offsetLerp
            }
            .offset(y = (-16).dp),
        verticalArrangement = Arrangement.Top
    ) {
        LoginTextInputs(
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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingHeader(
    num: Int,
    goBackToMain: () -> Unit,
    pagerState: PagerState,
    pagerOffset: Float
) {
    val coroutineScope = rememberCoroutineScope()
    val interactionSource = MutableInteractionSource()
    // Top Bar & Text
    Column(
        modifier = Modifier
            .graphicsLayer {
                val pageOffset = 1f - pagerOffset.coerceIn(-1f, 1f).absoluteValue
                alpha = pageOffset.pow(3)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 21.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
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
                                3 -> {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(2)
                                    }
                                }
                            }
                        },
                        interactionSource = interactionSource,
                        indication = null
                    )
                    .clip(CircleShape)
            )
            if (num == 3)
                Text(
                    text = "Skip",
                    color = colorResource(R.color.black),
                    textStyle = TextStyle.HEADER_H4,
                    modifier = Modifier
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) {
                            OnboardingRepository.saveOnboardingInfo(true)
                            overrideStatusBarColor(eateryBlueColor, ColorType.INTERP)
                        }
                        .height(14.dp)
                )
        }
        Text(
            text = when (num) {
                0 -> "Upcoming Menus"
                1 -> "Favorites"
                2 -> "Wait Times"
                else -> "Log in with Eatery"
            },
            textStyle = TextStyle.HEADER_H2,
            color = colorResource(id = R.color.eateryBlue),
            modifier = Modifier.padding(start = 16.dp)
        )
        Text(
            text = when (num) {
                0 -> "See menus by date and plan ahead"
                1 -> "Save and quickly find eateries and items"
                2 -> "Check for crowds in real time to avoid lines"
                else -> "See your meal swipes, BRBs, and more"
            },
            textStyle = TextStyle.APPDEV_BODY_MEDIUM,
            color = colorResource(id = R.color.gray06),
            modifier = Modifier.padding(top = 7.dp, start = 16.dp)
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingNextButton(num: Int, pagerState: PagerState, pagerOffset: Float) {
    val coroutineScope = rememberCoroutineScope()
    val interactionSource = MutableInteractionSource()

    // Next Button
    Surface(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
            .height(48.dp)
            .graphicsLayer {
                val pageOffset = 1f - pagerOffset.coerceIn(-1f, 1f).absoluteValue
                alpha = pageOffset.pow(3)
            }
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