package com.brelio.feature.clients.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.brelio.core.mvi.MviViewModel
import com.brelio.domain.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientDetailViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle,
) : MviViewModel<ClientDetailState, ClientDetailEvent, ClientDetailEffect>(ClientDetailState()) {

    private val clientId: String = checkNotNull(savedStateHandle["clientId"])

    init {
        loadClient()
    }

    override fun onEvent(event: ClientDetailEvent) {
        when (event) {
            ClientDetailEvent.DeleteClicked -> sendEffect(ClientDetailEffect.ShowDeleteConfirmation)
            ClientDetailEvent.ConfirmDelete -> deleteClient()
            ClientDetailEvent.BackClicked -> sendEffect(ClientDetailEffect.NavigateBack)
        }
    }

    private fun loadClient() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            clientRepository.getClient(clientId)
                .onSuccess { client ->
                    setState { copy(isLoading = false, client = client) }
                }
                .onFailure { throwable ->
                    setState { copy(isLoading = false, error = throwable.message) }
                }
        }
    }

    private fun deleteClient() {
        viewModelScope.launch {
            clientRepository.deleteClient(clientId)
                .onSuccess { sendEffect(ClientDetailEffect.NavigateBack) }
                .onFailure { throwable ->
                    sendEffect(ClientDetailEffect.ShowError(throwable.message.orEmpty()))
                }
        }
    }
}
