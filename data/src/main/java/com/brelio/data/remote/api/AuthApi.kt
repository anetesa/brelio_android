package com.brelio.data.remote.api

import com.brelio.data.remote.dto.AuthResponse
import com.brelio.data.remote.dto.RefreshTokenRequest
import com.brelio.data.remote.dto.ResetPasswordRequest
import com.brelio.data.remote.dto.SignInRequest
import com.brelio.data.remote.dto.SignUpRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    @POST("auth/v1/token")
    suspend fun signIn(
        @Query("grant_type") grantType: String = "password",
        @Body body: SignInRequest,
    ): AuthResponse

    @POST("auth/v1/signup")
    suspend fun signUp(@Body body: SignUpRequest): AuthResponse

    @POST("auth/v1/token")
    suspend fun refreshToken(
        @Query("grant_type") grantType: String = "refresh_token",
        @Body body: RefreshTokenRequest,
    ): AuthResponse

    @POST("auth/v1/recover")
    suspend fun resetPassword(@Body body: ResetPasswordRequest)

    @POST("auth/v1/logout")
    suspend fun signOut(@Header("Authorization") token: String)
}
