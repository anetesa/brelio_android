package com.brelio.core.network

import com.brelio.BuildConfig
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val authTokenManager: AuthTokenManager,
) : Authenticator {

    private val json = Json { ignoreUnknownKeys = true }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= MAX_RETRY_COUNT) {
            runBlocking { authTokenManager.clearTokens() }
            return null
        }

        val refreshToken = runBlocking { authTokenManager.getRefreshTokenSync() } ?: run {
            runBlocking { authTokenManager.clearTokens() }
            return null
        }

        val newTokens = refreshAccessToken(refreshToken)
        return if (newTokens != null) {
            runBlocking {
                authTokenManager.saveTokens(newTokens.accessToken, newTokens.refreshToken)
            }
            response.request.newBuilder()
                .header(HEADER_AUTHORIZATION, "$BEARER_PREFIX ${newTokens.accessToken}")
                .build()
        } else {
            runBlocking { authTokenManager.clearTokens() }
            null
        }
    }

    private fun refreshAccessToken(refreshToken: String): TokenResponse? {
        return try {
            val url = "${BuildConfig.SUPABASE_URL}/auth/v1/token?grant_type=refresh_token"
            val body = json.encodeToString(
                RefreshRequest(refreshToken = refreshToken),
            ).toRequestBody(JSON_MEDIA_TYPE)

            val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader(HEADER_API_KEY, BuildConfig.SUPABASE_ANON_KEY)
                .addHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                .build()

            val client = OkHttpClient.Builder().build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                response.body?.string()?.let { responseBody ->
                    json.decodeFromString<TokenResponse>(responseBody)
                }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    @Serializable
    private data class RefreshRequest(
        @SerialName("refresh_token") val refreshToken: String,
    )

    @Serializable
    private data class TokenResponse(
        @SerialName("access_token") val accessToken: String,
        @SerialName("refresh_token") val refreshToken: String,
    )

    private companion object {
        const val MAX_RETRY_COUNT = 2
        const val HEADER_AUTHORIZATION = "Authorization"
        const val HEADER_API_KEY = "apikey"
        const val HEADER_CONTENT_TYPE = "Content-Type"
        const val CONTENT_TYPE_JSON = "application/json"
        const val BEARER_PREFIX = "Bearer"
        val JSON_MEDIA_TYPE = "application/json".toMediaType()
    }
}
