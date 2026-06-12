package com.brelio.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.brelio.feature.home.components.AppointmentRow
import com.brelio.feature.home.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAppointmentClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading && state.appointments.isEmpty()) {
        LoadingScreen(modifier = modifier)
        return
    }

    PullToRefreshBox(
        isRefreshing = state.isLoading,
        onRefresh = { viewModel.onEvent(HomeEvent.Refresh) },
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = dimensionResource(R.dimen.spacing_lg),
                vertical = dimensionResource(R.dimen.spacing_lg),
            ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_lg)),
        ) {
            item {
                Text(
                    text = stringResource(R.string.today_appointments),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            item {
                StatsRow(state = state)
            }

            if (state.appointments.isEmpty()) {
                item {
                    EmptyAppointments()
                }
            } else {
                items(
                    items = state.appointments,
                    key = { it.id },
                ) { appointment ->
                    AppointmentRow(
                        appointment = appointment,
                        onClick = { onAppointmentClick(appointment.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsRow(
    state: HomeState,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
    ) {
        item {
            StatCard(
                label = stringResource(R.string.stat_revenue),
                value = String.format("%.0f", state.stats.todayRevenue),
            )
        }
        item {
            StatCard(
                label = stringResource(R.string.stat_clients),
                value = state.stats.totalClients.toString(),
            )
        }
        item {
            StatCard(
                label = stringResource(R.string.stat_appointments),
                value = state.stats.todayAppointments.toString(),
            )
        }
        item {
            StatCard(
                label = stringResource(R.string.stat_no_shows),
                value = state.stats.noShowCount.toString(),
            )
        }
    }
}

@Composable
private fun EmptyAppointments(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.spacing_xxxl) * 4),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.no_appointments_today),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
