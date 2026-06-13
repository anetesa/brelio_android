package com.brelio.feature.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.brelio.core.designsystem.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault())

@Composable
fun DateNavigationBar(
    selectedDate: LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.spacing_sm),
                vertical = dimensionResource(R.dimen.spacing_md),
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onPreviousDay) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.content_description_previous_day),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }

        Text(
            text = selectedDate.format(DATE_FORMATTER),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        IconButton(onClick = onNextDay) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.content_description_next_day),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
