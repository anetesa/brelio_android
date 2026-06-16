package com.brelio.feature.clients.list

import com.brelio.domain.model.Client

data class ClientsState(
    val clients: List<Client> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
)

sealed interface ClientsEvent {
    data class SearchQueryChanged(val query: String) : ClientsEvent
    data class ClientClicked(val clientId: String) : ClientsEvent
    data object AddClientClicked : ClientsEvent
    data object Refresh : ClientsEvent
}

sealed interface ClientsEffect {
    data class NavigateToDetail(val clientId: String) : ClientsEffect
    data object NavigateToAddClient : ClientsEffect
}
