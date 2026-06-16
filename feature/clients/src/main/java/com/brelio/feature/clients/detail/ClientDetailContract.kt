package com.brelio.feature.clients.detail

import com.brelio.domain.model.Client

data class ClientDetailState(
    val client: Client? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

sealed interface ClientDetailEvent {
    data object DeleteClicked : ClientDetailEvent
    data object ConfirmDelete : ClientDetailEvent
    data object BackClicked : ClientDetailEvent
}

sealed interface ClientDetailEffect {
    data object NavigateBack : ClientDetailEffect
    data class ShowError(val message: String) : ClientDetailEffect
    data object ShowDeleteConfirmation : ClientDetailEffect
}
