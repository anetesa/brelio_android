package com.brelio.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.brelio.core.designsystem.R
import com.brelio.domain.model.ThemeMode

@Composable
fun BrelioTheme(
    themeMode: ThemeMode = ThemeMode.Female,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (themeMode) {
        ThemeMode.Female -> lightColorScheme(
            primary = colorResource(R.color.female_primary),
            secondary = colorResource(R.color.female_accent),
            background = colorResource(R.color.female_background),
            surface = colorResource(R.color.female_surface),
            error = colorResource(R.color.female_error),
            onPrimary = colorResource(R.color.female_on_primary),
            onSecondary = colorResource(R.color.female_text),
            onBackground = colorResource(R.color.female_text),
            onSurface = colorResource(R.color.female_text),
            onError = colorResource(R.color.female_surface),
            outline = colorResource(R.color.female_border),
            surfaceVariant = colorResource(R.color.female_surface),
            onSurfaceVariant = colorResource(R.color.female_muted),
        )
        ThemeMode.Male -> darkColorScheme(
            primary = colorResource(R.color.male_primary),
            secondary = colorResource(R.color.male_accent),
            background = colorResource(R.color.male_background),
            surface = colorResource(R.color.male_surface),
            error = colorResource(R.color.male_error),
            onPrimary = colorResource(R.color.male_on_primary),
            onSecondary = colorResource(R.color.male_text),
            onBackground = colorResource(R.color.male_text),
            onSurface = colorResource(R.color.male_text),
            onError = colorResource(R.color.male_surface),
            outline = colorResource(R.color.male_border),
            surfaceVariant = colorResource(R.color.male_surface),
            onSurfaceVariant = colorResource(R.color.male_muted),
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BrelioTypography,
        shapes = brelioShapes(),
        content = content,
    )
}
