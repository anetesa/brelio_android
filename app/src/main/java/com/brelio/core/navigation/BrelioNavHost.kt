package com.brelio.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.brelio.R
import com.brelio.core.network.AuthTokenManager
import com.brelio.core.ui.LoadingScreen
import com.brelio.data.local.PreferencesManager
import com.brelio.feature.auth.reset.ResetPasswordScreen
import com.brelio.feature.auth.signin.SignInScreen
import com.brelio.feature.auth.signup.SignUpScreen
import com.brelio.feature.calendar.CalendarScreen
import com.brelio.feature.clients.detail.ClientDetailScreen
import com.brelio.feature.clients.list.ClientsScreen
import com.brelio.feature.home.HomeScreen
import com.brelio.feature.onboarding.OnboardingScreen
import com.brelio.feature.settings.SettingsScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class TabItem(
    val route: Routes,
    val labelResId: Int,
    val icon: ImageVector,
)

private val tabs = listOf(
    TabItem(Routes.Home, R.string.tab_home, Icons.Outlined.Home),
    TabItem(Routes.Calendar, R.string.tab_calendar, Icons.Outlined.CalendarMonth),
    TabItem(Routes.Requests, R.string.tab_requests, Icons.Outlined.MailOutline),
    TabItem(Routes.Clients, R.string.tab_clients, Icons.Outlined.People),
    TabItem(Routes.Settings, R.string.tab_settings, Icons.Outlined.Settings),
)

@HiltViewModel
class StartDestinationViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val authTokenManager: AuthTokenManager,
) : ViewModel() {

    private val _startDestination = MutableStateFlow<Routes?>(null)
    val startDestination: StateFlow<Routes?> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val isOnboarded = preferencesManager.hasOnboarded.first()
            val isAuthenticated = authTokenManager.accessToken.first() != null
            _startDestination.value = when {
                !isOnboarded -> Routes.Onboarding
                !isAuthenticated -> Routes.SignIn
                else -> Routes.MainTabs
            }
        }
    }
}

@Composable
fun BrelioNavHost(
    viewModel: StartDestinationViewModel = hiltViewModel(),
) {
    val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()

    val destination = startDestination
    if (destination == null) {
        LoadingScreen()
        return
    }

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = destination,
    ) {
        composable<Routes.Onboarding> {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Routes.SignIn) {
                        popUpTo(Routes.Onboarding) { inclusive = true }
                    }
                },
            )
        }
        composable<Routes.SignIn> {
            SignInScreen(
                onNavigateToSignUp = {
                    navController.navigate(Routes.SignUp)
                },
                onNavigateToResetPassword = {
                    navController.navigate(Routes.ResetPassword)
                },
                onNavigateToHome = {
                    navController.navigate(Routes.MainTabs) {
                        popUpTo(Routes.SignIn) { inclusive = true }
                    }
                },
            )
        }
        composable<Routes.SignUp> {
            SignUpScreen(
                onNavigateToSignIn = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Routes.MainTabs) {
                        popUpTo(Routes.SignIn) { inclusive = true }
                    }
                },
            )
        }
        composable<Routes.ResetPassword> {
            ResetPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }
        composable<Routes.MainTabs> {
            MainTabsScreen(
                onNavigateToClientDetail = { clientId ->
                    navController.navigate(Routes.ClientDetail(clientId))
                },
            )
        }
        composable<Routes.ClientDetail> {
            ClientDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }
    }
}

@Composable
private fun MainTabsScreen(
    onNavigateToClientDetail: (String) -> Unit,
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = stringResource(tab.labelResId),
                            )
                        },
                        label = { Text(text = stringResource(tab.labelResId)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = colorResource(R.color.tab_active),
                            selectedTextColor = colorResource(R.color.tab_active),
                            unselectedIconColor = colorResource(R.color.tab_inactive),
                            unselectedTextColor = colorResource(R.color.tab_inactive),
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        val modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when (tabs[selectedTabIndex].route) {
            Routes.Home -> HomeScreen(
                onAppointmentClick = { },
                modifier = modifier,
            )
            Routes.Calendar -> CalendarScreen(
                onAppointmentClick = { },
                modifier = modifier,
            )
            Routes.Requests -> PlaceholderScreen(stringResource(R.string.tab_requests), modifier)
            Routes.Clients -> ClientsScreen(
                onClientClick = onNavigateToClientDetail,
                onAddClientClick = { },
                modifier = modifier,
            )
            Routes.Settings -> SettingsScreen(
                onSignedOut = { },
                modifier = modifier,
            )
            else -> Unit
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}
