package com.brelio.feature.calendar.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import com.brelio.core.designsystem.R

@Composable
fun TimeSlot(
    hour: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.calendar_hour_height)),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .width(dimensionResource(R.dimen.calendar_time_gutter_width)),
            contentAlignment = Alignment.TopEnd,
        ) {
            Text(
                text = String.format("%02d:00", hour),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopStart,
        ) {
            HorizontalDivider(
                color = colorResource(R.color.calendar_slot_border),
                thickness = dimensionResource(R.dimen.divider_thickness),
            )
        }
    }
}
