package com.brelio.feature.auth.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brelio.core.designsystem.R
import com.brelio.core.ui.BrelioButton
import com.brelio.core.ui.BrelioOutlinedButton
import com.brelio.core.ui.BrelioTextField

@Composable
fun SignInScreen(
    onNavigateToSignUp: () -> Unit,
    onNavigateToResetPassword: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SignInEffect.NavigateToHome -> onNavigateToHome()
                SignInEffect.NavigateToSignUp -> onNavigateToSignUp()
                SignInEffect.NavigateToResetPassword -> onNavigateToResetPassword()
                is SignInEffect.LaunchGoogleSignIn -> {
                    launchGoogleSignIn(context, effect.webClientId) { idToken ->
                        viewModel.onEvent(SignInEvent.GoogleIdTokenReceived(idToken))
                    }
                }
                is SignInEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = dimensionResource(R.dimen.spacing_xl)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xxxl)))

                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))

                Text(
                    text = stringResource(R.string.sign_in),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xxl)))

                BrelioTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEvent(SignInEvent.EmailChanged(it)) },
                    label = stringResource(R.string.email_label),
                    placeholder = stringResource(R.string.email_hint),
                    keyboardType = KeyboardType.Email,
                    errorResId = state.emailError,
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

                BrelioTextField(
                    value = state.password,
                    onValueChange = { viewModel.onEvent(SignInEvent.PasswordChanged(it)) },
                    label = stringResource(R.string.password_label),
                    placeholder = stringResource(R.string.password_hint),
                    isPassword = true,
                    errorResId = state.passwordError,
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))

                TextButton(
                    onClick = { viewModel.onEvent(SignInEvent.ForgotPasswordClicked) },
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(
                        text = stringResource(R.string.forgot_password),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

                BrelioButton(
                    text = stringResource(R.string.sign_in_button),
                    onClick = { viewModel.onEvent(SignInEvent.SignInClicked) },
                    isLoading = state.isLoading,
                    enabled = !state.isLoading,
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                    Text(
                        text = stringResource(R.string.or_divider),
                        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.spacing_lg)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

                BrelioOutlinedButton(
                    text = stringResource(R.string.sign_in_with_google),
                    onClick = { viewModel.onEvent(SignInEvent.GoogleSignInClicked) },
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xxl)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.dont_have_account),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(
                        onClick = { viewModel.onEvent(SignInEvent.SignUpClicked) },
                    ) {
                        Text(
                            text = stringResource(R.string.sign_up),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xxl)))
            }
        }
    }
}

private suspend fun launchGoogleSignIn(
    context: android.content.Context,
    webClientId: String,
    onToken: (String) -> Unit,
) {
    try {
        val credentialManager = androidx.credentials.CredentialManager.create(context)
        val googleIdOption = com.google.android.libraries.identity.googleid.GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .build()
        val request = androidx.credentials.GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        val result = credentialManager.getCredential(context as android.app.Activity, request)
        val googleIdToken = com.google.android.libraries.identity.googleid
            .GoogleIdTokenCredential.createFrom(result.credential.data)
        onToken(googleIdToken.idToken)
    } catch (_: Exception) {
        // cancelled by user or no accounts configured
    }
}
