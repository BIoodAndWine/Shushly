package com.shushly.app.presentation.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.shushly.app.domain.model.DnsProvider
import com.shushly.app.domain.model.Region
import com.shushly.app.presentation.theme.ShushlyCrimson
import com.shushly.app.presentation.theme.ShushlyEmerald
import com.shushly.app.presentation.theme.ShushlySapphire
import com.shushly.app.presentation.viewmodel.AdvancedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedScreen(
    viewModel: AdvancedViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showCustomDohDialog by remember { mutableStateOf(false) }
    var selectingForPrimary by remember { mutableStateOf<Boolean?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Advanced Mode",
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
            // Header Description
            Text(
                text = "Configure custom primary and backup DNS resolvers, test response latency, and enable automatic failover.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Primary DNS Card
            val primaryProvider = uiState.allProviders.firstOrNull { it.id == uiState.draftConfig.primaryProviderId }
            val primaryCustomUrl = uiState.draftConfig.primaryCustomDohUrl
            val primaryLatency = uiState.latencies[uiState.draftConfig.primaryProviderId]

            DnsSlotCard(
                slotTitle = "Primary DNS",
                providerName = primaryCustomUrl ?: (primaryProvider?.name ?: "Control D"),
                filterOrRegion = if (primaryCustomUrl != null) "Custom HTTPS DoH" else "${primaryProvider?.filterName} · ${primaryProvider?.region?.displayName}",
                latencyMs = primaryLatency,
                isCustom = primaryCustomUrl != null,
                onChangeClick = { selectingForPrimary = true }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary DNS Card
            val secondaryProvider = uiState.allProviders.firstOrNull { it.id == uiState.draftConfig.secondaryProviderId }
            val secondaryCustomUrl = uiState.draftConfig.secondaryCustomDohUrl
            val secondaryLatency = uiState.latencies[uiState.draftConfig.secondaryProviderId]

            DnsSlotCard(
                slotTitle = "Secondary DNS (Backup Failover)",
                providerName = secondaryCustomUrl ?: (secondaryProvider?.name ?: "AdGuard DNS"),
                filterOrRegion = if (secondaryCustomUrl != null) "Custom HTTPS DoH" else "${secondaryProvider?.filterName} · ${secondaryProvider?.region?.displayName}",
                latencyMs = secondaryLatency,
                isCustom = secondaryCustomUrl != null,
                onChangeClick = { selectingForPrimary = false }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Region selection chips
            Text(
                text = "Recommended for Your Region",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Region.entries) { region ->
                    val isSelected = uiState.draftConfig.selectedRegion == region
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) ShushlySapphire.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) ShushlySapphire else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.setRegion(region) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = region.displayName,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) ShushlySapphire else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Latency Speed Test Action
            Button(
                onClick = { viewModel.testAllLatencies() },
                enabled = !uiState.isTestingLatency,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (uiState.isTestingLatency) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = ShushlySapphire,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Measuring Latency...", fontSize = 14.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Test Speed",
                            tint = ShushlySapphire,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Test DNS Response Speeds", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Add Custom DoH URL Action
            Button(
                onClick = { showCustomDohDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Custom DNS",
                        tint = ShushlySapphire,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Custom DNS-over-HTTPS Endpoint", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Save & Apply Button
            Button(
                onClick = {
                    viewModel.saveAndApply(onSaved = onNavigateBack)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ShushlySapphire),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Save & Apply Configuration",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Provider Selector Sheet / Modal
    if (selectingForPrimary != null) {
        val isPrimary = selectingForPrimary == true
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { selectingForPrimary = null },
            title = {
                Text(
                    text = if (isPrimary) "Select Primary DNS" else "Select Secondary DNS",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    uiState.allProviders.forEach { provider ->
                        val isSelected = if (isPrimary) {
                            uiState.draftConfig.primaryProviderId == provider.id && uiState.draftConfig.primaryCustomDohUrl == null
                        } else {
                            uiState.draftConfig.secondaryProviderId == provider.id && uiState.draftConfig.secondaryCustomDohUrl == null
                        }
                        val latency = uiState.latencies[provider.id]

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    if (isPrimary) viewModel.setPrimaryProvider(provider.id)
                                    else viewModel.setSecondaryProvider(provider.id)
                                    selectingForPrimary = null
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = provider.name,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 15.sp,
                                    color = if (isSelected) ShushlySapphire else MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "${provider.filterName} · ${provider.region.displayName}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (latency != null && latency > 0) {
                                Text(
                                    text = "$latency ms",
                                    fontSize = 12.sp,
                                    color = ShushlyEmerald,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = ShushlySapphire,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectingForPrimary = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Custom DoH Dialog
    if (showCustomDohDialog) {
        var addAsPrimary by remember { mutableStateOf(true) }

        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showCustomDohDialog = false },
            title = {
                Text("Add Custom DNS-over-HTTPS", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Enter a valid HTTPS endpoint. Shushly will run a live RFC-compliant DNS test to verify filtering and certificate validity.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = uiState.customDohInput,
                        onValueChange = { viewModel.onCustomDohInputChanged(it) },
                        placeholder = { Text("https://example.com/dns-query", fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = uiState.customDohError != null
                    )
                    if (uiState.customDohError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = uiState.customDohError ?: "",
                            color = ShushlyCrimson,
                            fontSize = 12.sp
                        )
                    }
                    if (uiState.customDohSuccess) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Resolver verified successfully!",
                            color = ShushlyEmerald,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Assign to:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (addAsPrimary) ShushlySapphire else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { addAsPrimary = true }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    "Primary",
                                    color = if (addAsPrimary) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (!addAsPrimary) ShushlySapphire else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { addAsPrimary = false }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    "Secondary",
                                    color = if (!addAsPrimary) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.validateAndAddCustomDoh(asPrimary = addAsPrimary)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShushlySapphire)
                ) {
                    Text("Verify & Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDohDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DnsSlotCard(
    slotTitle: String,
    providerName: String,
    filterOrRegion: String,
    latencyMs: Long?,
    isCustom: Boolean,
    onChangeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = slotTitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (latencyMs != null && latencyMs > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(ShushlyEmerald)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$latencyMs ms",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShushlyEmerald
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = providerName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = filterOrRegion,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onChangeClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                ) {
                    Text("Change", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
