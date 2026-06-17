package com.brelio.feature.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brelio.core.designsystem.R
import com.brelio.core.ui.ScreenHeader
import com.brelio.domain.model.ThemeMode

@Composable
fun SettingsScreen(
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }

    if (state.showSignOutDialog) {
        SignOutConfirmationDialog(
            onConfirm = { viewModel.onEvent(SettingsEvent.ConfirmSignOut) },
            onDismiss = { viewModel.onEvent(SettingsEvent.DismissDialog) },
        )
    }

    if (showLanguageDialog) {
        LanguageDialog(
            currentCode = state.languageCode,
            onLanguageSelected = { code ->
                showLanguageDialog = false
                viewModel.onEvent(SettingsEvent.LanguageChanged(code))
            },
            onDismiss = { showLanguageDialog = false },
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            ScreenHeader(title = stringResource(R.string.settings_title))
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimensionResource(R.dimen.spacing_lg)),
        ) {
            SectionHeader(title = stringResource(R.string.settings_appearance))

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))

            ThemeSelector(
                selectedTheme = state.themeMode,
                onThemeSelected = { viewModel.onEvent(SettingsEvent.ThemeChanged(it)) },
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

            SectionHeader(title = stringResource(R.string.settings_language))

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))

            SettingsRow(
                title = stringResource(R.string.settings_language),
                subtitle = languageDisplayName(state.languageCode),
                onClick = { showLanguageDialog = true },
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

            SectionHeader(title = stringResource(R.string.settings_account))

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))

            SettingsRow(
                title = stringResource(R.string.settings_sign_out),
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_md)),
                    )
                },
                titleColor = MaterialTheme.colorScheme.error,
                onClick = { viewModel.onEvent(SettingsEvent.SignOutClicked) },
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xxl)))
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(vertical = dimensionResource(R.dimen.spacing_xs)),
    )
}

@Composable
private fun ThemeSelector(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
    ) {
        ThemeCard(
            title = stringResource(R.string.theme_classic),
            isSelected = selectedTheme == ThemeMode.Female,
            previewColors = listOf(
                colorResource(R.color.female_primary),
                colorResource(R.color.female_accent),
                colorResource(R.color.female_background),
            ),
            onClick = { onThemeSelected(ThemeMode.Female) },
            modifier = Modifier.weight(1f),
        )
        ThemeCard(
            title = stringResource(R.string.theme_modern),
            isSelected = selectedTheme == ThemeMode.Male,
            previewColors = listOf(
                colorResource(R.color.male_primary),
                colorResource(R.color.male_accent),
                colorResource(R.color.male_background),
            ),
            onClick = { onThemeSelected(ThemeMode.Male) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ThemeCard(
    title: String,
    isSelected: Boolean,
    previewColors: List<androidx.compose.ui.graphics.Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(dimensionResource(R.dimen.radius_md)),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_md)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs)),
            ) {
                previewColors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.icon_lg))
                            .clip(CircleShape)
                            .background(color),
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs)),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_sm)),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: @Composable (() -> Unit)? = null,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = dimensionResource(R.dimen.spacing_md)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_md)))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = titleColor,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_md)),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SignOutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.sign_out_confirm_title))
        },
        text = {
            Text(text = stringResource(R.string.sign_out_confirm_message))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.settings_sign_out),
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

private data class LanguageOption(
    val code: String,
    val displayName: String,
)

private val LANGUAGES = listOf(
    LanguageOption("en", "English"),
    LanguageOption("pl", "Polski"),
    LanguageOption("ru", "Русский"),
    LanguageOption("uk", "Українська"),
    LanguageOption("cs", "Čeština"),
    LanguageOption("sk", "Slovenčina"),
    LanguageOption("hu", "Magyar"),
    LanguageOption("de", "Deutsch"),
)

@Composable
private fun LanguageDialog(
    currentCode: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.settings_language))
        },
        text = {
            Column {
                LANGUAGES.forEach { lang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(lang.code) }
                            .padding(vertical = dimensionResource(R.dimen.spacing_md)),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = lang.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (lang.code == currentCode) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                            color = if (lang.code == currentCode) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            modifier = Modifier.weight(1f),
                        )
                        if (lang.code == currentCode) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(dimensionResource(R.dimen.icon_md)),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    if (lang != LANGUAGES.last()) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.action_cancel))
            }
        },
    )
}

private fun languageDisplayName(code: String): String {
    return LANGUAGES.find { it.code == code }?.displayName ?: code
}
