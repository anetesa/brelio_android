package com.brelio.core.navigation

import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable data object Onboarding : Routes
    @Serializable data object SignIn : Routes
    @Serializable data object SignUp : Routes
    @Serializable data object ResetPassword : Routes
    @Serializable data object MainTabs : Routes
    @Serializable data object Home : Routes
    @Serializable data object Calendar : Routes
    @Serializable data object Requests : Routes
    @Serializable data object Clients : Routes
    @Serializable data object Settings : Routes
    @Serializable data class ClientDetail(val clientId: String) : Routes
}
