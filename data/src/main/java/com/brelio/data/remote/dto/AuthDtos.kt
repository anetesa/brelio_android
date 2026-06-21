package com.brelio.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignInRequest(
    val email: String,
    val password: String,
)

@Serializable
data class SignUpRequest(
    val email: String,
    val password: String,
)

@Serializable
data class RefreshTokenRequest(
    @SerialName("refresh_token") val refreshToken: String,
)

@Serializable
data class IdTokenRequest(
    val provider: String,
    @SerialName("id_token") val idToken: String,
)

@Serializable
data class ResetPasswordRequest(
    val email: String,
)

@Serializable
data class AuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("user") val user: UserDto,
)

@Serializable
data class UserDto(
    val id: String,
    val email: String? = null,
)
