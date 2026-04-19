package com.cornellappdev.android.eatery.data

import com.cornellappdev.android.eatery.data.repositories.AuthTokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Provider

/**
 * OkHttp interceptor that automatically adds a Bearer token to protected requests.
 *
 * Uses [Provider] to avoid a circular dependency with [AuthTokenRepository].
 */
class AuthInterceptor @Inject constructor(
    private val authTokenRepositoryProvider: Provider<AuthTokenRepository>
) : Interceptor {

    companion object {
        private val PUBLIC_ENDPOINTS = setOf(
            "/eateries/",
            "/auth/verify-token",
            "/auth/refresh-token"
        )
    }

    /**
     * Intercepts outgoing HTTP requests.
     * Adds the Bearer token to protected requests.
     * If the response is 401 or 403, refreshes tokens and retries once.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (!isPublicEndpoint(request)) {
            val requestWithToken = addTokenToRequest(request)
            var response = chain.proceed(requestWithToken)

            if (response.code == 401 || response.code == 403) {
                response.close()
                try {
                    runBlocking {
                        authTokenRepositoryProvider.get().refreshTokens()
                    }
                    val retryRequest = addTokenToRequest(request)
                    response = chain.proceed(retryRequest)
                } catch (_: Exception) {
                    return Response.Builder()
                        .code(response.code)
                        .message(response.message)
                        .request(request)
                        .protocol(response.protocol)
                        .build()
                }
            }
            return response
        }
        return chain.proceed(request)
    }

    private fun addTokenToRequest(request: Request): Request {
        return try {
            val token = runBlocking {
                authTokenRepositoryProvider.get().getAccessToken()
            }
            request.newBuilder()
                .header("Authorization", token)
                .build()
        } catch (_: Exception) {
            request
        }
    }

    private fun isPublicEndpoint(request: Request): Boolean {
        val path = request.url.encodedPath
        return PUBLIC_ENDPOINTS.any { path.startsWith(it) }
    }
}

