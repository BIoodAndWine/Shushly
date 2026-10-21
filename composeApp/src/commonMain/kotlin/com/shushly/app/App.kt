package com.shushly.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.shushly.app.data.repository.BillingRepository
import com.shushly.app.data.repository.DnsDirectoryRepository
import com.shushly.app.data.repository.SettingsRepository
import com.shushly.app.domain.engine.DnsProtectionController
import com.shushly.app.presentation.screens.HomeScreen
import com.shushly.app.presentation.screens.PremiumScreen
import com.shushly.app.presentation.screens.SettingsScreen
import com.shushly.app.presentation.theme.ShushlyTheme
import com.shushly.app.presentation.viewmodel.HomeViewModel
import com.shushly.app.presentation.viewmodel.PremiumViewModel
import com.shushly.app.presentation.viewmodel.SettingsViewModel

enum class Screen {
    HOME,
    PREMIUM,
    SETTINGS
}

@Composable
fun App(
    protectionController: DnsProtectionController,
    settingsRepository: SettingsRepository = remember { SettingsRepository() },
    billingRepository: BillingRepository = remember { BillingRepository(settingsRepository) },
    directoryRepository: DnsDirectoryRepository = remember { DnsDirectoryRepository() },
    modifier: Modifier = Modifier
) {
    val homeViewModel = remember {
        HomeViewModel(protectionController, settingsRepository, billingRepository)
    }
    val premiumViewModel = remember {
        PremiumViewModel(directoryRepository, settingsRepository, billingRepository, protectionController)
    }
    val settingsViewModel = remember {
        SettingsViewModel(settingsRepository, protectionController)
    }

    val themeMode by settingsRepository.themeMode.collectAsState()
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    ShushlyTheme(themeMode = themeMode) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                Screen.HOME -> HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToPremium = { currentScreen = Screen.PREMIUM },
                    onNavigateToSettings = { currentScreen = Screen.SETTINGS },
                    modifier = modifier
                )
                Screen.PREMIUM -> PremiumScreen(
                    viewModel = premiumViewModel,
                    onNavigateBack = { currentScreen = Screen.HOME },
                    modifier = modifier
                )
                Screen.SETTINGS -> SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { currentScreen = Screen.HOME },
                    onNavigateToPremium = { currentScreen = Screen.PREMIUM },
                    modifier = modifier
                )
            }
        }
    }
}
