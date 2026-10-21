package com.shushly.app.desktop

import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.shushly.app.App
import com.shushly.app.data.repository.SettingsRepository
import com.shushly.app.desktop.engine.WindowsDnsProtectionController

fun main() = application {
    val settingsRepository = SettingsRepository()
    val protectionController = WindowsDnsProtectionController(initialConfig = settingsRepository.configuration.value)

    Window(
        onCloseRequest = ::exitApplication,
        title = "Shushly",
        state = WindowState(
            position = WindowPosition(Alignment.Center),
            size = DpSize(440.dp, 760.dp)
        ),
        resizable = false
    ) {
        App(
            protectionController = protectionController,
            settingsRepository = settingsRepository
        )
    }
}
