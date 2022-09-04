package com.appdev.eateryblueandroid.ui.components.login

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.viewinterop.AndroidView
import com.appdev.eateryblueandroid.ui.viewmodels.LoginFailureType
import com.appdev.eateryblueandroid.ui.viewmodels.ProfileViewModel
import com.appdev.eateryblueandroid.util.Constants
import com.appdev.eateryblueandroid.util.LoginRepository

const val TAG = "GetLoginWebView"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginWebView(profileViewModel: ProfileViewModel) {
    val state = profileViewModel.state.collectAsState()
    CookieManager.getInstance().removeAllCookies(null)
    CookieManager.getInstance().flush()

    state.value.let { loginState ->
        if (loginState is ProfileViewModel.State.LoggingIn || loginState is ProfileViewModel.State.AutoLoggingIn) {
            AndroidView(factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(0, 0)
                    settings.javaScriptEnabled = true
                    webViewClient = SessionIdWebViewClient(
                        netid = (loginState as? ProfileViewModel.State.LoggingIn)?.netid
                            ?: (loginState as ProfileViewModel.State.AutoLoggingIn).netid,
                        password = (loginState as? ProfileViewModel.State.LoggingIn)?.password
                            ?: (loginState as ProfileViewModel.State.AutoLoggingIn).password,
                        loginSuccess = { sessionId ->
                            profileViewModel.loginSuccess(sessionId)
                            if (state.value !is ProfileViewModel.State.AutoLoggingIn)
                                LoginRepository.saveLoginInfo(
                                    (state.value as ProfileViewModel.State.LoggingIn).netid,
                                    (state.value as ProfileViewModel.State.LoggingIn).password
                                )
                        },
                        loginFailure = profileViewModel::loginFailure,
                        webpageLoaded = profileViewModel::webpageLoaded
                    )
                    loadUrl(Constants.SESSIONID_WEBVIEW_URL)
                }
            }, update = {
                it.loadUrl(Constants.SESSIONID_WEBVIEW_URL)
            })
        }
    }
}

class SessionIdWebViewClient(
    val netid: String,
    val password: String,
    val loginSuccess: (sessionId: String) -> Unit,
    val loginFailure: (error: LoginFailureType) -> Unit,
    val webpageLoaded: () -> Unit
) : WebViewClient() {

    var attempts = 0
    var lastUrl: String? = null

    override fun onPageFinished(view: WebView?, url: String?) {
        if (lastUrl == url) return
        Log.i(TAG, url.toString())
        lastUrl = url
        if (url?.contains("sessionId=") == true) {
            val idx = url.indexOf("sessionId=") + 10
            val modified = url.substring(idx)
            val endIdx =
                if (modified.indexOf('&') == -1) modified.length
                else modified.indexOf('&')
            loginSuccess(modified.substring(0, endIdx))
        } else if (attempts > 1) {
            loginFailure(LoginFailureType.USERNAME_PASSWORD_INVALID)
        } else if (url?.contains(Constants.SESSIONID_CU_LOGIN_SUBSTRING_IDENTIFIER) == true) {
            val script = """
                document.getElementsByName('j_username')[0].value = '${netid}';
                document.getElementsByName('j_password')[0].value = '${password}';
                document.getElementsByName('_eventId_proceed')[0].click();
            """.trimIndent()
            webpageLoaded()
            view?.evaluateJavascript(script) { attempts += 1 }
        }
        super.onPageFinished(view, url)
    }
}