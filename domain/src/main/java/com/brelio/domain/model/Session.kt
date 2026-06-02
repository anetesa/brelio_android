package com.brelio.domain.model

data class Session(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val email: String,
)
