package com.cornellappdev.android.eateryblue.ui.components.login

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.cornellappdev.android.eateryblue.BuildConfig
import com.cornellappdev.android.eateryblue.ui.components.general.CustomTextField
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GraySix
import com.cornellappdev.android.eateryblue.ui.theme.GrayThree
import com.cornellappdev.android.eateryblue.ui.theme.GrayZero
import com.cornellappdev.android.eateryblue.ui.viewmodels.LoginViewModel

@Composable
fun LoginPage(
    loginState: LoginViewModel.State.Login,
    loginViewModel: LoginViewModel,
    onWrongCredentials: () -> Unit = {}
) {
    val passwordFocus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val clickable =
        loginState.netid.isNotEmpty() && loginState.password.isNotEmpty() && !loginState.loading

    Column(
        modifier = Modifier
            .zIndex(1f)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Log in with Eatery",
            style = EateryBlueTypography.h3,
            color = EateryBlue
        )

        Text(
            text = "See your meal swipes, BRBs, and more",
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
            color = GraySix,
            modifier = Modifier.padding(top = 7.dp)
        )
        Text(
            text = "NetID",
            style = EateryBlueTypography.h5,
            color = Color.Black,
            modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
        )

        CustomTextField(
            value = loginState.netid,
            onValueChange = {
                loginViewModel.onNetIDTyped(it)
            },
            enabled = !loginState.loading,
            placeholder = "Type your NetID (e.g. abc123)",
            backgroundColor = GrayZero,
            onSubmit = {
                if (loginState.netid.isNotEmpty()) passwordFocus.requestFocus()
            }
        )

        Text(
            text = "Password",
            style = EateryBlueTypography.h5,
            color = Color.Black,
            modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
        )

        CustomTextField(
            value = loginState.password,
            onValueChange = {
                loginViewModel.onPasswordTyped(it)
            },
            enabled = !loginState.loading,
            placeholder = "Type your password...",
            backgroundColor = GrayZero,
            focusRequester = passwordFocus,
            isPassword = true,
            onSubmit = {
                if (loginState.password.isNotEmpty()) {
                    focusManager.clearFocus()
                }
            }
        )
        Button(
            enabled = clickable,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 66.dp)
                .height(56.dp),
            onClick = {
                focusManager.clearFocus()
                loginViewModel.onLoginPressed()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (clickable) EateryBlue else GrayZero
            ),
            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = if (loginState.loading) "Logging in..." else "Log in",
                color = if (clickable) Color.White else GrayThree,
                style = EateryBlueTypography.h5
            )
        }
        if (loginState.loading) {
            // LoginWebView should take up no space whatsoever. User should not be able to see it.
            Box(
                modifier = Modifier
                    .size(0.dp)
                    .zIndex(-1f)
            ) {
                LoginWebView(
                    netId = loginState.netid,
                    password = loginState.password,
                    onSuccess = { sessionId ->
                        loginViewModel.getUser(sessionId)
                        loginViewModel.onLoginPressed()
                    },
                    onWrongCredentials = {
                        onWrongCredentials()
                    })
            }
        }
    }
}

@Composable
fun LoginWebView(
    netId: String,
    password: String,
    onSuccess: (String) -> Unit,
    onWrongCredentials: () -> Unit
) {
    CookieManager.getInstance().removeAllCookies(null)
    CookieManager.getInstance().flush()

    AndroidView(factory = {
        WebView(it).apply {
            visibility = View.INVISIBLE
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.javaScriptEnabled = true
            webViewClient =
                CustomWebViewClient(
                    netId = netId,
                    password = password,
                    onSuccess = onSuccess,
                    onWrongCredentials = onWrongCredentials,
                )
            loadUrl(BuildConfig.SESSIONID_WEBVIEW_URL)
        }
    }, update = {
        it.loadUrl(BuildConfig.SESSIONID_WEBVIEW_URL)
    })
}

class CustomWebViewClient(
    private val netId: String,
    private val password: String,
    val onSuccess: (String) -> Unit,
    val onWrongCredentials: () -> Unit
) : WebViewClient() {
    private var attempts = 0
    private var lastUrl: String? = null

    override fun onPageFinished(view: WebView?, url: String?) {
        if (lastUrl == url) return
        lastUrl = url
        if (url?.contains("sessionId=") == true) {
            val sessionToken = url.substringAfter("sessionId=").removeSuffix("&")
            Log.d("SessionID", sessionToken)
            onSuccess(sessionToken)
        } else if (attempts > 1) {
            onWrongCredentials()
        } else if (url?.contains("shibidp") == true) {
            // Injects the username and password into their respective spots on the
            // login form.
            val script = """
                document.getElementsByName('j_username')[0].value = '${netId}';
                document.getElementsByName('j_password')[0].value = '${password}';
                document.getElementsByName('_eventId_proceed')[0].click();
                """.trimIndent()
            view?.evaluateJavascript(script) { attempts += 1 }
        }
        super.onPageFinished(view, url)
    }
}
