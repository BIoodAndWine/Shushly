package com.shushly.app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shushly.app.presentation.theme.ShushlySapphire
import com.shushly.app.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAdvanced: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // General Section
            SettingsSectionHeader(title = "General")

            SettingsToggleRow(
                title = "Reconnect Automatically",
                subtitle = "Resume protection when switching between Wi-Fi and mobile networks",
                checked = uiState.configuration.autoReconnect,
                onCheckedChange = { viewModel.setAutoReconnect(it) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SettingsToggleRow(
                title = "Start on Device Boot",
                subtitle = "Enable protection automatically when device starts",
                checked = uiState.configuration.startOnBoot,
                onCheckedChange = { viewModel.setStartOnBoot(it) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SettingsToggleRow(
                title = "UI Sound Effects",
                subtitle = "Play subtle audio cues on connection and mode change",
                checked = uiState.soundEnabled,
                onCheckedChange = { viewModel.setSoundEnabled(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Appearance Section
            SettingsSectionHeader(title = "Appearance")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Follow System Appearance",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (uiState.themeMode.equals("system", ignoreCase = true))
                            "Automatically adapting to system dark/light theme"
                        else
                            "Currently locked to ${uiState.themeMode.replaceFirstChar { it.uppercase() }} mode",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val isSystem = uiState.themeMode.equals("system", ignoreCase = true)
                Switch(
                    checked = isSystem,
                    onCheckedChange = { checked ->
                        if (checked) {
                            viewModel.setThemeMode("system")
                        } else {
                            viewModel.setThemeMode("dark")
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ShushlySapphire
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Advanced Mode Section
            SettingsSectionHeader(title = "Advanced DNS")

            SettingsActionRow(
                title = "Advanced Mode",
                subtitle = if (uiState.configuration.isCustomConfig) "Custom primary and backup resolvers active" else "Configure custom resolvers, backup failover & test speed",
                onClick = onNavigateToAdvanced
            )

            Spacer(modifier = Modifier.height(10.dp))

            SettingsActionRow(
                title = "Restore Default Settings",
                subtitle = "Reset all DNS endpoints to default Light mode",
                onClick = { viewModel.restoreDefaultConfiguration() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // About & Privacy Section
            SettingsSectionHeader(title = "Privacy & About")

            SettingsActionRow(
                title = "Privacy Policy",
                subtitle = "Zero query logs, zero browsing history collected",
                onClick = { showPrivacyDialog = true }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SettingsActionRow(
                title = "About Shushly",
                subtitle = "Version ${uiState.appVersion} · Free public DNS-over-HTTPS",
                onClick = { showAboutDialog = true }
            )
        }
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Shushly is built with a zero-knowledge architecture:\n\n" +
                    "• No user accounts or registrations required.\n" +
                    "• No DNS query logs, browsing histories, or IP tracking are saved or shared.\n" +
                    "• No third-party advertising or analytics SDKs.\n" +
                    "• DNS queries are encrypted via standard DNS-over-HTTPS (RFC 8484) and routed directly to the selected public resolvers.\n" +
                    "• Upstream DNS providers handle queries according to their own published zero-logging policies."
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Understood")
                }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Shushly", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Shushly is a free, lightweight, privacy-respecting cross-platform DNS ad blocker built with Kotlin Multiplatform and Compose Multiplatform.\n\n" +
                        "Modes:\n" +
                        "• Light (Default): Control D Ads & Tracking\n" +
                        "• Balanced: Control D OISD Full\n" +
                        "• Strict: Control D HaGeZi Pro\n" +
                        "• Advanced Mode: Custom primary/secondary DNS, regional recommendations, and speed testing.\n\n" +
                        "Version 1.0.0"
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        color = ShushlySapphire,
        modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)
    )
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ShushlySapphire
            )
        )
    }
}

@Composable
private fun SettingsActionRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}
