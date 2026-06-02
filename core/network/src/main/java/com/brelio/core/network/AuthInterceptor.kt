package com.brelio.core.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val authTokenManager: AuthTokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { authTokenManager.getAccessTokenSync() }
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader(HEADER_AUTHORIZATION, "$BEARER_PREFIX $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }

    private companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
        const val BEARER_PREFIX = "Bearer"
    }
}
