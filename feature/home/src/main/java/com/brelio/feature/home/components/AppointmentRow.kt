package com.brelio.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.brelio.core.designsystem.R
import com.brelio.core.ui.BrelioCard
import com.brelio.domain.model.Appointment
import com.brelio.domain.model.AppointmentStatus
import com.brelio.domain.model.PaymentStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun AppointmentRow(
    appointment: Appointment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BrelioCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_lg)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
        ) {
            Column {
                Text(
                    text = appointment.startAt.format(TIME_FORMATTER),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = appointment.endAt.format(TIME_FORMATTER),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = appointment.clientName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                appointment.serviceName?.let { service ->
                    Text(
                        text = service,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            StatusChip(status = appointment.status)
        }
    }
}

@Composable
private fun StatusChip(
    status: AppointmentStatus,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = when (status) {
        AppointmentStatus.Confirmed -> colorResource(R.color.appointment_confirmed)
        AppointmentStatus.Pending -> colorResource(R.color.appointment_pending)
        AppointmentStatus.Cancelled -> colorResource(R.color.appointment_cancelled)
        AppointmentStatus.NoShow -> colorResource(R.color.appointment_no_show)
        AppointmentStatus.Completed -> colorResource(R.color.appointment_completed)
    }

    val label = when (status) {
        AppointmentStatus.Confirmed -> stringResource(R.string.status_confirmed)
        AppointmentStatus.Pending -> stringResource(R.string.status_pending)
        AppointmentStatus.Cancelled -> stringResource(R.string.status_cancelled)
        AppointmentStatus.NoShow -> stringResource(R.string.status_no_show)
        AppointmentStatus.Completed -> stringResource(R.string.status_completed)
    }

    Box(
        modifier = modifier
            .background(
                color = backgroundColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(dimensionResource(R.dimen.radius_xl)),
            )
            .padding(
                horizontal = dimensionResource(R.dimen.spacing_md),
                vertical = dimensionResource(R.dimen.spacing_xs),
            ),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = backgroundColor,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppointmentRowPreview() {
    AppointmentRow(
        appointment = Appointment(
            id = "1",
            clientId = "c1",
            clientName = "Anna Kowalska",
            stylistId = null,
            stylistName = null,
            serviceName = "Strzyżenie damskie",
            startAt = LocalDateTime.of(2026, 6, 20, 10, 0),
            endAt = LocalDateTime.of(2026, 6, 20, 11, 0),
            status = AppointmentStatus.Confirmed,
            notes = null,
            paymentStatus = PaymentStatus.Unpaid,
            paymentMethod = null,
            services = emptyList(),
        ),
        onClick = {},
    )
}
