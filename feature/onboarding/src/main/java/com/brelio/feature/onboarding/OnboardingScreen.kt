package com.brelio.feature.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brelio.core.designsystem.R
import com.brelio.core.ui.BrelioButton

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { state.pageCount })

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OnboardingEffect.NavigateToSignIn -> onComplete()
            }
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            viewModel.onEvent(OnboardingEvent.PageChanged(page))
        }
    }

    LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    val pages = listOf(
        OnboardingPage(
            icon = Icons.Default.CalendarMonth,
            titleResId = R.string.onboarding_title_1,
            descriptionResId = R.string.onboarding_desc_1,
        ),
        OnboardingPage(
            icon = Icons.Default.People,
            titleResId = R.string.onboarding_title_2,
            descriptionResId = R.string.onboarding_desc_2,
        ),
        OnboardingPage(
            icon = Icons.Default.BarChart,
            titleResId = R.string.onboarding_title_3,
            descriptionResId = R.string.onboarding_desc_3,
        ),
    )

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = dimensionResource(R.dimen.spacing_lg),
                        end = dimensionResource(R.dimen.spacing_sm),
                    ),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(
                    onClick = { viewModel.onEvent(OnboardingEvent.SkipClicked) },
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_skip),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                OnboardingPageContent(page = pages[page])
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.spacing_xl)),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(state.pageCount) { index ->
                    val color by animateColorAsState(
                        targetValue = if (index == state.currentPage) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                        label = "indicator_color",
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = dimensionResource(R.dimen.onboarding_indicator_spacing))
                            .size(dimensionResource(R.dimen.onboarding_indicator_size))
                            .clip(CircleShape)
                            .background(color),
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xxl)))

            val isLastPage = state.currentPage == state.pageCount - 1

            BrelioButton(
                text = if (isLastPage) {
                    stringResource(R.string.onboarding_get_started)
                } else {
                    stringResource(R.string.onboarding_next)
                },
                onClick = {
                    if (isLastPage) {
                        viewModel.onEvent(OnboardingEvent.GetStartedClicked)
                    } else {
                        viewModel.onEvent(OnboardingEvent.NextClicked)
                    }
                },
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.spacing_xl)),
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xxxl)))
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.spacing_xl)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = page.icon,
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen.onboarding_icon_size)),
            tint = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xxl)))

        Text(
            text = stringResource(page.titleResId),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

        Text(
            text = stringResource(page.descriptionResId),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

private data class OnboardingPage(
    val icon: ImageVector,
    val titleResId: Int,
    val descriptionResId: Int,
)
