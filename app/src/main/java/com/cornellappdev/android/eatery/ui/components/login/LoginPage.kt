package com.cornellappdev.android.eatery.ui.components.login

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.cornellappdev.android.eatery.BuildConfig
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.ui.theme.EateryBlue
import com.cornellappdev.android.eatery.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eatery.ui.theme.GraySix
import com.cornellappdev.android.eatery.ui.theme.GrayThree
import com.cornellappdev.android.eatery.ui.theme.GrayZero
import com.cornellappdev.android.eatery.util.EateryPreview
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

/**
 * Login page that prompts the user to log in to their GET account.
 * [isLoading] is whether the login button should show a loading state.
 * [onLoginPressed] is called when the user clicks the login button.
 * [onSuccess] is called with the sessionID when the user has successfully logged in, and we
 * have grabbed the sessionID from the validation page after log in.
 * [onBackClick] is called when the user clicks the back button.
 * [onModalHidden] is called when the web view modal is hidden while the user has not logged in.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(
    isLoading: Boolean,
    onLoginPressed: () -> Unit,
    onSuccess: (String) -> Unit,
    onBackClick: () -> Unit,
    onModalHidden: () -> Unit
) {
    var loggedIn by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }
    var isFirstWebView by remember { mutableStateOf(true) }
    if (loggedIn) {
        showSheet = false
    } else if (!sheetState.isVisible && isLoading) {
        showSheet = true
    }
    if (!isPreview()) {
        Box(modifier = Modifier.statusBarsPadding()) {
            LoginPageMainLayer(onBackClick, isLoading, onLoginPressed)
            if (showSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showSheet = false
                        onModalHidden()
                    },
                    sheetState = sheetState,
                    shape = RoundedCornerShape(
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp,
                        topStart = 12.dp,
                        topEnd = 12.dp
                    )
                ) {
                    LoginWebView(
                        onLoggedIn = { loggedIn = true },
                        onSuccess = onSuccess,
                        isFirstWebView = isFirstWebView,
                        webViewLoaded = { isFirstWebView = false }
                    )
                }
            }
        }
    } else {
        LoginPageMainLayer(onBackClick, isLoading, onLoginPressed)
    }
}

@Composable
private fun LoginPageMainLayer(
    onBackClick: () -> Unit,
    loading: Boolean,
    onLoginPressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = 7.dp,
                start = 16.dp,
                end = 16.dp
            )
            .then(Modifier.statusBarsPadding())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 2.dp)
                .height(34.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(24.dp, 24.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_left_chevron),
                    contentDescription = "Back Arrow"
                )
            }
        }
        Text(
            text = "Log into Eatery",
            color = EateryBlue,
            style = EateryBlueTypography.h3
        )
        val shimmer = rememberShimmer(ShimmerBounds.View)
        val shimmerModifier =
            if (loading) Modifier.shimmer(customShimmer = shimmer) else Modifier
        val clickable = !loading

        Column(
            modifier = Modifier.zIndex(1f)
        ) {
            Text(
                text = "Log in with your Cornell NetID to see your account balance and history",
                style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
                color = GraySix,
                modifier = Modifier.padding(top = 7.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_eaterylogo),
                    contentDescription = "Eatery logo",
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight(),
                    colorFilter = ColorFilter.tint(Color(0xFFB7D3F3))
                )
            }
            Button(
                enabled = clickable,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .then(shimmerModifier)
                    .height(56.dp),
                onClick = {
                    onLoginPressed()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (clickable) EateryBlue else GrayZero,
                    disabledContainerColor = GrayZero
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = if (loading) "Logging in..." else "Log in",
                    color = if (clickable) Color.White else GrayThree,
                    style = EateryBlueTypography.h5
                )
            }
        }
    }
}

/**
 * Web view that handles NetID login. [onLoggedIn] is called when the user has logged in.
 * [onSuccess] is called after [onLoggedIn] when we have grabbed the sessionID from the
 * validation page after log in.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun LoginWebView(
    onLoggedIn: () -> Unit,
    onSuccess: (String) -> Unit,
    isFirstWebView: Boolean,
    webViewLoaded: () -> Unit
) {
    if (isFirstWebView) {
        // If the web view is being loaded for the first time after user navigates to LoginPage,
        // then reset cookies. This makes logging out work.
        // We need the conditional since LoginWebView gets recomposed during login.
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
        webViewLoaded()
    }
    AndroidView(
        factory = {
            WebView(it).apply {
                visibility = View.VISIBLE
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                // Necessary for displaying normal login webpage behavior.
                settings.javaScriptEnabled = true
                webViewClient = CustomWebViewClient(onSuccess, onLoggedIn)
                loadUrl(BuildConfig.SESSIONID_WEBVIEW_URL)
            }
        }
    )
}

private class CustomWebViewClient(
    val onSuccess: (String) -> Unit,
    val onLoggedIn: () -> Unit
) : WebViewClient() {
    private var lastUrl: String? = null
    override fun onPageFinished(view: WebView?, url: String?) {
        if (lastUrl == url) return
        lastUrl = url
        if (url?.contains("sessionId=") == true) {
            val sessionToken = url.substringAfter("sessionId=").removeSuffix("&")
            onSuccess(sessionToken)
        } else if (url?.contains("eventId_proceed") == true) {
            // page after successful login, before validation page.
            // hides webview before validation page
            onLoggedIn()
        }
        super.onPageFinished(view, url)
    }
}


@Preview
@Composable
private fun LoginPagePreview() = EateryPreview {
    LoginPage(
        isLoading = false,
        onLoginPressed = {},
        onSuccess = {},
        onBackClick = {},
        onModalHidden = {}
    )
}

@Composable
private fun isPreview() = LocalInspectionMode.current