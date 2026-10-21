package com.shushly.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shushly.app.presentation.components.AdvancedModeEntry
import com.shushly.app.presentation.components.ProtectionLevelDropdown
import com.shushly.app.presentation.components.ProtectionStatusText
import com.shushly.app.presentation.components.ProtectionToggle
import com.shushly.app.presentation.components.ShushlyHeader
import com.shushly.app.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAdvanced: () -> Unit,
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
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            ShushlyHeader(onSettingsClick = onNavigateToSettings)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 440.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                Spacer(modifier = Modifier.height(6.dp))

                // Primary Connection Pill Switch (Cloudflare WARP-inspired)
                ProtectionToggle(
                    status = uiState.status,
                    onToggle = { viewModel.toggleProtection() }
                )

                // Simplified Connection Status Messaging
                ProtectionStatusText(status = uiState.status)

                // Compact Custom Protection Level Dropdown (Light / Balanced / Strict)
                ProtectionLevelDropdown(
                    selectedLevel = if (uiState.status.isProtected) uiState.status.activeLevel else uiState.configuration.protectionLevel,
                    onLevelSelected = { level -> viewModel.selectProtectionLevel(level) }
                )

                // Understated Advanced Mode Entry Row
                AdvancedModeEntry(
                    isCustomConfig = uiState.configuration.isCustomConfig,
                    onClick = onNavigateToAdvanced
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
