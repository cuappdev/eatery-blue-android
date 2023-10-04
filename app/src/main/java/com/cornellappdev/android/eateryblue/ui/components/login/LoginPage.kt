package com.cornellappdev.android.eateryblue.ui.components.login

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.eateryblue.BuildConfig
import com.cornellappdev.android.eateryblue.data.models.User
import com.cornellappdev.android.eateryblue.ui.components.general.CustomTextField
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlue
import com.cornellappdev.android.eateryblue.ui.theme.EateryBlueTypography
import com.cornellappdev.android.eateryblue.ui.theme.GrayThree
import com.cornellappdev.android.eateryblue.ui.theme.GrayZero
import com.cornellappdev.android.eateryblue.ui.viewmodels.LoggedInStatus
import com.cornellappdev.android.eateryblue.ui.viewmodels.LoginState
import com.cornellappdev.android.eateryblue.ui.viewmodels.LoginViewModel

@Composable
fun LoginPage(
    loginViewModel: LoginViewModel = hiltViewModel(),
    autoLogin: Boolean = true,
    onSuccess: (User) -> Unit,
    onError: () -> Unit,
    onAutoLoginFail: () -> Unit = {},
    onWrongCredentials: () -> Unit = {}
) {
    val passwordFocus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var netId by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var attemptSignIn by rememberSaveable { mutableStateOf(false) }
    val clickable = netId.isNotEmpty() && password.isNotEmpty() && !attemptSignIn
    var tryCacheLogin by rememberSaveable { mutableStateOf(autoLogin) }

    // TODO test cache log in
    // Tries to login using credentials that's stored locally.
    if (tryCacheLogin) {
        when (loginViewModel.isLoggedIn) {
            LoggedInStatus.Pending -> {

            }

            LoggedInStatus.NotLoggedIn -> {
                onAutoLoginFail()
                tryCacheLogin = false
            }

            is LoggedInStatus.IsLoggedIn -> {
                val loginInfo = loginViewModel.isLoggedIn as LoggedInStatus.IsLoggedIn
                netId = loginInfo.username
                password = loginInfo.password

                // Will activate the LoginWebView
                attemptSignIn = true
            }
        }
    } else {
        Column(modifier = Modifier.zIndex(1f)) {
            Text(
                text = "NetID",
                style = EateryBlueTypography.h5,
                color = Color.Black,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )

            CustomTextField(
                value = netId,
                onValueChange = {
                    netId = it
                },
                enabled = !attemptSignIn,
                placeholder = "Type your NetID (e.g. abc123)",
                backgroundColor = GrayZero,
                onSubmit = {
                    if (netId.isNotEmpty()) passwordFocus.requestFocus()
                }
            )

            Text(
                text = "Password",
                style = EateryBlueTypography.h5,
                color = Color.Black,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )

            CustomTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                enabled = !attemptSignIn,
                placeholder = "Type your password...",
                backgroundColor = GrayZero,
                focusRequester = passwordFocus,
                isPassword = true,
                onSubmit = {
                    if (password.isNotEmpty()) {
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
                    attemptSignIn = true
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (clickable) EateryBlue else GrayZero
                ),
                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = if (attemptSignIn) "Logging in..." else "Log in",
                    color = if (clickable) Color.White else GrayThree,
                    style = EateryBlueTypography.h5
                )
            }
        }

        if (attemptSignIn) {
            // LoginWebView should take up no space whatsoever. User should not be able to see it.
            Box(
                modifier = Modifier
                    .size(0.dp)
                    .zIndex(-2f)
            ) {
                LoginWebView(netId = netId, password = password, onSuccess = { sessionId ->
                    loginViewModel.getUser(sessionId)
                    attemptSignIn = false
                }, onWrongCredentials = {
                    onWrongCredentials()
                    if (!tryCacheLogin) {
                        // Toast is only shown for non-cache login attempts
                        showWrongCredentialsToast(context)
                    } else {
                        // If the login attempt was from credentials stored locally, they
                        // were incorrect and the user needs to login again.
                        netId = ""
                        password = ""
                    }
                    tryCacheLogin = false
                    attemptSignIn = false
                })
            }
        }

        when (loginViewModel.loginState) {
            LoginState.Pending -> {

            }

            LoginState.Error -> {
                onError()
                TODO("user endpoint doesn't exist on backend so logging in is failing everytime")
            }

            is LoginState.Success -> {
                loginViewModel.saveLoginInfo(netId, password)
                onSuccess((loginViewModel.loginState as LoginState.Success).user)
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
            layoutParams = ViewGroup.LayoutParams(0, 0)
            settings.javaScriptEnabled = true
            webViewClient = CustomWebViewClient(
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
