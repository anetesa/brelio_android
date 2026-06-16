package com.brelio.feature.clients.list

import androidx.lifecycle.viewModelScope
import com.brelio.core.mvi.MviViewModel
import com.brelio.domain.usecase.auth.ObserveSessionUseCase
import com.brelio.domain.usecase.client.GetClientsUseCase
import com.brelio.domain.usecase.client.SearchClientsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientsViewModel @Inject constructor(
    private val getClientsUseCase: GetClientsUseCase,
    private val searchClientsUseCase: SearchClientsUseCase,
    private val observeSessionUseCase: ObserveSessionUseCase,
) : MviViewModel<ClientsState, ClientsEvent, ClientsEffect>(ClientsState()) {

    private var searchJob: Job? = null

    init {
        loadClients()
    }

    override fun onEvent(event: ClientsEvent) {
        when (event) {
            is ClientsEvent.SearchQueryChanged -> {
                setState { copy(searchQuery = event.query) }
                debounceSearch(event.query)
            }
            is ClientsEvent.ClientClicked -> {
                sendEffect(ClientsEffect.NavigateToDetail(event.clientId))
            }
            is ClientsEvent.AddClientClicked -> {
                sendEffect(ClientsEffect.NavigateToAddClient)
            }
            is ClientsEvent.Refresh -> loadClients()
        }
    }

    private fun loadClients() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            val session = observeSessionUseCase().filterNotNull().first()

            getClientsUseCase(session.userId)
                .onSuccess { clients ->
                    setState { copy(isLoading = false, clients = clients) }
                }
                .onFailure { throwable ->
                    setState { copy(isLoading = false, error = throwable.message) }
                }
        }
    }

    private fun debounceSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            setState { copy(isLoading = true) }

            val session = observeSessionUseCase().filterNotNull().first()

            searchClientsUseCase(session.userId, query)
                .onSuccess { clients ->
                    setState { copy(isLoading = false, clients = clients) }
                }
                .onFailure { throwable ->
                    setState { copy(isLoading = false, error = throwable.message) }
                }
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 300L
    }
}
