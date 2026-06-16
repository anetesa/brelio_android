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
            is ClientDetailEvent.DeleteClicked -> {
                sendEffect(ClientDetailEffect.ShowDeleteConfirmation)
            }
            is ClientDetailEvent.ConfirmDelete -> deleteClient()
            is ClientDetailEvent.BackClicked -> {
                sendEffect(ClientDetailEffect.NavigateBack)
            }
        }
    }

    private fun loadClient() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            clientRepository.getClientById(clientId)
                .onSuccess { dto ->
                    if (dto != null) {
                        val client = com.brelio.domain.model.Client(
                            id = dto.id,
                            name = dto.name,
                            phone = dto.phone,
                            email = dto.email,
                            avatarUrl = dto.avatarUrl,
                            notes = dto.notes,
                            birthday = dto.birthday,
                            tags = dto.tags.orEmpty(),
                            noShowCount = dto.noShowCount ?: 0,
                            createdAt = dto.createdAt.orEmpty(),
                        )
                        setState { copy(isLoading = false, client = client) }
                    } else {
                        setState { copy(isLoading = false, error = "Client not found") }
                    }
                }
                .onFailure { throwable ->
                    setState { copy(isLoading = false, error = throwable.message) }
                }
        }
    }

    private fun deleteClient() {
        viewModelScope.launch {
            clientRepository.deleteClient(clientId)
                .onSuccess {
                    sendEffect(ClientDetailEffect.NavigateBack)
                }
                .onFailure { throwable ->
                    sendEffect(ClientDetailEffect.ShowError(throwable.message ?: "Delete failed"))
                }
        }
    }
}
