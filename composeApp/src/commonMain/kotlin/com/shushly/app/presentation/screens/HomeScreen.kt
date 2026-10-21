package com.shushly.app.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shushly.app.presentation.components.PremiumBanner
import com.shushly.app.presentation.components.ProtectionLevelSelector
import com.shushly.app.presentation.components.ProtectionStatusText
import com.shushly.app.presentation.components.ProtectionToggle
import com.shushly.app.presentation.components.ShushlyHeader
import com.shushly.app.presentation.theme.ShushlyCrimson
import com.shushly.app.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToPremium: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.feedbackMessage) {
        val msg = uiState.feedbackMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            ShushlyHeader(onSettingsClick = onNavigateToSettings)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Central Protection Toggle
            ProtectionToggle(
                status = uiState.status,
                onToggle = { viewModel.toggleProtection() }
            )

            // Status Headline & Subtitle
            ProtectionStatusText(status = uiState.status)

            Spacer(modifier = Modifier.height(16.dp))

            // Protection Level Selector (Default / Balanced / Strong)
            ProtectionLevelSelector(
                selectedLevel = uiState.configuration.protectionLevel,
                onLevelSelected = { level -> viewModel.selectProtectionLevel(level) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtle Premium Entry
            PremiumBanner(
                isPremium = uiState.isPremium,
                onClick = onNavigateToPremium
            )
        }
    }
}
