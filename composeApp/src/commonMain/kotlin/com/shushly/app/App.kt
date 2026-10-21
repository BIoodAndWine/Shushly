package com.shushly.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.shushly.app.data.repository.DnsDirectoryRepository
import com.shushly.app.data.repository.SettingsRepository
import com.shushly.app.domain.audio.SoundPlayer
import com.shushly.app.domain.audio.createPlatformSoundPlayer
import com.shushly.app.domain.engine.DnsProtectionController
import com.shushly.app.presentation.screens.AdvancedScreen
import com.shushly.app.presentation.screens.HomeScreen
import com.shushly.app.presentation.screens.SettingsScreen
import com.shushly.app.presentation.theme.ShushlyTheme
import com.shushly.app.presentation.viewmodel.AdvancedViewModel
import com.shushly.app.presentation.viewmodel.HomeViewModel
import com.shushly.app.presentation.viewmodel.SettingsViewModel

enum class Screen {
    HOME,
    ADVANCED,
    SETTINGS
}

@Composable
fun App(
    protectionController: DnsProtectionController,
    settingsRepository: SettingsRepository = remember { SettingsRepository() },
    directoryRepository: DnsDirectoryRepository = remember { DnsDirectoryRepository() },
    soundPlayer: SoundPlayer = remember { createPlatformSoundPlayer() },
    modifier: Modifier = Modifier
) {
    val homeViewModel = remember {
        HomeViewModel(protectionController, settingsRepository, soundPlayer)
    }
    val advancedViewModel = remember {
        AdvancedViewModel(directoryRepository, settingsRepository, protectionController)
    }
    val settingsViewModel = remember {
        SettingsViewModel(settingsRepository, protectionController)
    }

    val themeMode by settingsRepository.themeMode.collectAsState()
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    ShushlyTheme(themeMode = themeMode) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                (fadeIn(tween(220, easing = FastOutSlowInEasing)) + scaleIn(initialScale = 0.98f, animationSpec = tween(220)))
                    .togetherWith(
                        fadeOut(tween(180, easing = FastOutSlowInEasing)) + scaleOut(targetScale = 0.98f, animationSpec = tween(180))
                    )
            },
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                Screen.HOME -> HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToAdvanced = { currentScreen = Screen.ADVANCED },
                    onNavigateToSettings = { currentScreen = Screen.SETTINGS },
                    modifier = modifier
                )
                Screen.ADVANCED -> AdvancedScreen(
                    viewModel = advancedViewModel,
                    onNavigateBack = { currentScreen = Screen.HOME },
                    modifier = modifier
                )
                Screen.SETTINGS -> SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { currentScreen = Screen.HOME },
                    onNavigateToAdvanced = { currentScreen = Screen.ADVANCED },
                    modifier = modifier
                )
            }
        }
    }
}
