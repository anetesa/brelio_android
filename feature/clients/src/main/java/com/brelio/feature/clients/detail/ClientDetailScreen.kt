package com.brelio.feature.clients.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brelio.core.designsystem.R
import com.brelio.core.ui.LoadingScreen
import com.brelio.core.ui.ScreenHeader
import com.brelio.domain.model.Client
import com.brelio.feature.clients.components.ClientAvatar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClientDetailScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClientDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                showDeleteDialog = false
                viewModel.onEvent(ClientDetailEvent.ConfirmDelete)
            },
            onDismiss = { showDeleteDialog = false },
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            ScreenHeader(
                title = state.client?.name.orEmpty(),
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.content_description_delete),
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when {
            state.isLoading -> {
                LoadingScreen(modifier = Modifier.padding(innerPadding))
            }
            state.error != null -> {
                ErrorContent(
                    message = state.error.orEmpty(),
                    modifier = Modifier.padding(innerPadding),
                )
            }
            state.client != null -> {
                ClientDetailContent(
                    client = state.client!!,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@ExperimentalLayoutApi
@Composable
private fun ClientDetailContent(
    client: Client,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(R.dimen.spacing_lg)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ClientAvatar(
            name = client.name,
            avatarUrl = client.avatarUrl,
            size = dimensionResource(R.dimen.icon_avatar_lg),
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

        Text(
            text = client.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

        InfoSection(client = client)

        if (client.tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))
            TagsSection(tags = client.tags)
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

        StatsSection(client = client)
    }
}

@Composable
private fun InfoSection(
    client: Client,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
    ) {
        client.phone?.let { phone ->
            InfoRow(
                label = stringResource(R.string.client_phone),
                value = phone,
            )
        }
        client.email?.let { email ->
            InfoRow(
                label = stringResource(R.string.client_email),
                value = email,
            )
        }
        client.birthday?.let { birthday ->
            InfoRow(
                label = stringResource(R.string.client_birthday),
                value = birthday,
            )
        }
        client.notes?.let { notes ->
            InfoRow(
                label = stringResource(R.string.client_notes),
                value = notes,
            )
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = dimensionResource(R.dimen.spacing_sm)),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

@ExperimentalLayoutApi
@Composable
private fun TagsSection(
    tags: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs)),
        ) {
            tags.forEach { tag ->
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun StatsSection(
    client: Client,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        StatItem(
            label = stringResource(R.string.client_visits),
            value = "0",
        )
        StatItem(
            label = stringResource(R.string.client_no_shows),
            value = client.noShowCount.toString(),
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.action_delete))
        },
        text = {
            Text(text = stringResource(R.string.action_confirm))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.action_delete),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.action_cancel))
            }
        },
    )
}
