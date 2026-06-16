package com.brelio.feature.clients.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brelio.core.designsystem.R
import com.brelio.core.ui.LoadingScreen
import com.brelio.core.ui.ScreenHeader
import com.brelio.feature.clients.components.ClientRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen(
    onClientClick: (String) -> Unit,
    onAddClientClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClientsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            ScreenHeader(title = stringResource(R.string.clients_title))
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(ClientsEvent.AddClientClicked) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.content_description_add),
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onEvent(ClientsEvent.SearchQueryChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(R.dimen.spacing_lg),
                        vertical = dimensionResource(R.dimen.spacing_sm),
                    ),
                placeholder = { Text(stringResource(R.string.search_clients)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.content_description_search),
                    )
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
            )

            when {
                state.isLoading && state.clients.isEmpty() -> {
                    LoadingScreen()
                }
                state.clients.isEmpty() -> {
                    EmptyClients(
                        message = if (state.searchQuery.isNotEmpty()) {
                            stringResource(R.string.no_results)
                        } else {
                            stringResource(R.string.no_clients)
                        },
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = dimensionResource(R.dimen.spacing_lg),
                            vertical = dimensionResource(R.dimen.spacing_sm),
                        ),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
                    ) {
                        items(
                            items = state.clients,
                            key = { it.id },
                        ) { client ->
                            ClientRow(
                                client = client,
                                onClick = { onClientClick(client.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyClients(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.spacing_xl)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
