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
import com.shushly.app.presentation.theme.DarkBorder
import com.shushly.app.presentation.theme.ShushlyCrimson
import com.shushly.app.presentation.theme.ShushlyEmerald
import com.shushly.app.presentation.theme.ShushlySapphire
import com.shushly.app.presentation.viewmodel.PremiumViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    viewModel: PremiumViewModel,
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
                        text = "Your DNS Setup",
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
            // Unlock banner if free user
            if (!uiState.isPremium) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ShushlySapphire.copy(alpha = 0.12f))
                        .border(1.dp, ShushlySapphire, RoundedCornerShape(16.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Lifetime Premium",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "$5.00 USD",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShushlySapphire
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "One-time purchase. No subscriptions or hidden fees. Unlocks custom DNS providers, secondary backup failover, and regional recommendations.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { viewModel.purchaseLifetime() },
                            enabled = !uiState.isPurchasing,
                            colors = ButtonDefaults.buttonColors(containerColor = ShushlySapphire),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (uiState.isPurchasing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Upgrade to Lifetime Premium", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

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
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(Region.entries) { region ->
                    val isSelected = uiState.draftConfig.selectedRegion == region
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) ShushlySapphire else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { viewModel.setRegion(region) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = region.displayName,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action row: Test Latency + Add Custom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.testAllLatencies() },
                    enabled = !uiState.isTestingLatency,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (uiState.isTestingLatency) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = ShushlySapphire,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Speed",
                            tint = ShushlySapphire,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Speed", color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp)
                    }
                }

                Button(
                    onClick = { showCustomDohDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Custom",
                        tint = ShushlySapphire,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Custom", color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp)
                }
            }

            // Custom DoH input section
            AnimatedVisibility(visible = showCustomDohDialog) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Add Custom DNS-over-HTTPS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uiState.customDohInput,
                        onValueChange = { viewModel.onCustomDohInputChanged(it) },
                        placeholder = { Text("https://example.com/dns-query", fontSize = 13.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (uiState.customDohError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.customDohError ?: "",
                            color = ShushlyCrimson,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = { showCustomDohDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = { viewModel.validateAndAddCustomDoh(asPrimary = true) },
                            colors = ButtonDefaults.buttonColors(containerColor = ShushlySapphire)
                        ) {
                            Text("Validate & Use")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save and Apply Button
            Button(
                onClick = { viewModel.saveAndApply(onSaved = onNavigateBack) },
                colors = ButtonDefaults.buttonColors(containerColor = ShushlyEmerald),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Save & Apply",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Provider selection dialog / sheet if user clicked "Change"
            if (selectingForPrimary != null) {
                val isPrimary = selectingForPrimary == true
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = if (isPrimary) "Select Primary DNS" else "Select Secondary DNS",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    uiState.recommendedProviders.forEach { provider ->
                        val latency = uiState.latencies[provider.id]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    if (isPrimary) {
                                        viewModel.setPrimaryProvider(provider.id)
                                    } else {
                                        viewModel.setSecondaryProvider(provider.id)
                                    }
                                    selectingForPrimary = null
                                }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${provider.name} · ${provider.filterName}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = provider.description,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (latency != null && latency > 0) {
                                Text(
                                    text = "$latency ms",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ShushlyEmerald
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DnsSlotCard(
    slotTitle: String,
    providerName: String,
    filterOrRegion: String,
    latencyMs: Long?,
    isCustom: Boolean,
    onChangeClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = slotTitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = ShushlySapphire
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = providerName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = filterOrRegion,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (latencyMs != null && latencyMs > 0) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Measured: $latencyMs ms",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ShushlyEmerald
                    )
                }
            }

            Button(
                onClick = onChangeClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Change", color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp)
            }
        }
    }
}
