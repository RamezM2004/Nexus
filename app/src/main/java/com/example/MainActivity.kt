package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.LinkScreen
import com.example.model.PrototypeStateMode
import com.example.ui.components.LinkTopBar
import com.example.ui.components.PrototypeStateSwitcherBar
import com.example.ui.components.ThemeColorStudioDialog
import com.example.ui.screens.ClipboardScreen
import com.example.ui.screens.ConnectionSecurityScreen
import com.example.ui.screens.FileTransferScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MediaScreen
import com.example.ui.screens.PresentationScreen
import com.example.ui.screens.RemoteControlScreen
import com.example.ui.theme.CuratedThemePalettes
import com.example.ui.theme.LinkTheme
import com.example.ui.theme.LocalLinkPalette
import com.example.ui.theme.buildCustomHslPalette
import com.example.viewmodel.LinkViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LinkCompanionRoot()
        }
    }
}

@Composable
fun LinkCompanionRoot(
    viewModel: LinkViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val themeCfg = uiState.themeConfig

    val activePalette = if (themeCfg.useCustomHues) {
        buildCustomHslPalette(
            primaryHue = themeCfg.customPrimaryHue,
            secondaryHue = themeCfg.customSecondaryHue,
            deepOledDark = themeCfg.deepOledDark
        )
    } else {
        val preset = CuratedThemePalettes.find { it.id == themeCfg.selectedPresetId }
            ?: CuratedThemePalettes.first()
        if (themeCfg.deepOledDark) {
            preset.copy(
                background = androidx.compose.ui.graphics.Color(0xFF09090F),
                surface = androidx.compose.ui.graphics.Color(0xFF12121C),
                surfaceElevated = androidx.compose.ui.graphics.Color(0xFF1B1B29)
            )
        } else {
            preset
        }
    }

    LinkTheme(palette = activePalette) {
        val palette = LocalLinkPalette.current

        BackHandler(enabled = uiState.currentScreen != LinkScreen.HOME) {
            viewModel.navigateBack()
        }

        if (themeCfg.isThemeStudioOpen) {
            ThemeColorStudioDialog(
                themeConfig = themeCfg,
                onSelectPreset = { viewModel.selectThemePreset(it) },
                onUpdatePrimaryHue = { viewModel.updateCustomPrimaryHue(it) },
                onUpdateSecondaryHue = { viewModel.updateCustomSecondaryHue(it) },
                onToggleDeepOled = { viewModel.toggleDeepOledDark() },
                onDismiss = { viewModel.toggleThemeStudio(false) }
            )
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(palette.background),
            containerColor = palette.background,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                Column {
                    LinkTopBar(
                        currentScreen = uiState.currentScreen,
                        stateMode = uiState.stateMode,
                        deviceName = uiState.laptopInfo.deviceName,
                        onNavigateBack = { viewModel.navigateBack() },
                        onOpenSecurity = { viewModel.navigateTo(LinkScreen.SECURITY) },
                        onOpenThemeStudio = { viewModel.toggleThemeStudio(true) }
                    )
                }
            },
            bottomBar = {
                PrototypeStateSwitcherBar(
                    currentMode = uiState.stateMode,
                    onSelectMode = { viewModel.setPrototypeStateMode(it) },
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        ) { innerPadding ->
            AnimatedContent(
                targetState = uiState.currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(160))
                },
                label = "link_screen_transition",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { screen ->
                when (screen) {
                    LinkScreen.HOME -> {
                        HomeScreen(
                            uiState = uiState,
                            onToggleConnection = { viewModel.toggleConnection() },
                            onNavigateToScreen = { viewModel.navigateTo(it) },
                            onOpenThemeStudio = { viewModel.toggleThemeStudio(true) },
                            onRestoreSampleData = { viewModel.restoreSampleData() },
                            onRetryFailedTransfers = { viewModel.retryFailedTransfers() },
                            onDismissBanner = { viewModel.dismissBannerMessage() }
                        )
                    }

                    LinkScreen.FILES -> {
                        FileTransferScreen(
                            uiState = uiState,
                            onSelectFolder = { viewModel.selectFolder(it) },
                            onUploadSimulated = { name, size -> viewModel.queueSimulatedUpload(name, size) },
                            onDownloadFolderFile = { viewModel.queueFolderFileDownload(it) },
                            onRetryFailedTransfers = { viewModel.retryFailedTransfers() },
                            onToggleConnection = { viewModel.toggleConnection() },
                            onRestoreSampleData = { viewModel.restoreSampleData() },
                            onNavigateToScreen = { viewModel.navigateTo(it) },
                            onDismissBanner = { viewModel.dismissBannerMessage() }
                        )
                    }

                    LinkScreen.REMOTE -> {
                        RemoteControlScreen(
                            uiState = uiState,
                            onTrackpadMove = { dx, dy -> viewModel.onTrackpadMove(dx, dy) },
                            onMouseButtonClick = { viewModel.onMouseButtonClick(it) },
                            onVerticalScroll = { viewModel.onVerticalScroll(it) },
                            onSensitivityChange = { viewModel.onSensitivityChange(it) },
                            onToggleExpandedTrackpad = { viewModel.toggleExpandedTrackpad() },
                            onToggleModifier = { viewModel.toggleKeyboardModifier(it) },
                            onSendShortcut = { viewModel.sendQuickShortcut(it) },
                            onUpdateKeyboardDraft = { viewModel.updateKeyboardDraft(it) },
                            onSendKeyboardDraft = { viewModel.sendKeyboardDraft() },
                            onTogglePlayPause = { viewModel.togglePlayPause() },
                            onSkipPrevious = { viewModel.skipPreviousTrack() },
                            onSkipNext = { viewModel.skipNextTrack() },
                            onToggleMute = { viewModel.toggleMediaMute() },
                            onOpenFullMedia = { viewModel.navigateTo(LinkScreen.MEDIA) },
                            onOpenPresentationMode = { viewModel.navigateTo(LinkScreen.PRESENTATION) },
                            onToggleConnection = { viewModel.toggleConnection() },
                            onRestoreSampleData = { viewModel.restoreSampleData() },
                            onNavigateToScreen = { viewModel.navigateTo(it) },
                            onDismissBanner = { viewModel.dismissBannerMessage() }
                        )
                    }

                    LinkScreen.PRESENTATION -> {
                        PresentationScreen(
                            uiState = uiState,
                            onNextSlide = { viewModel.nextSlide() },
                            onPreviousSlide = { viewModel.previousSlide() },
                            onToggleTimer = { viewModel.togglePresentationTimer() },
                            onResetTimer = { viewModel.resetPresentationTimer() },
                            onSelectPointerMode = { viewModel.selectPointerMode(it) },
                            onUpdateLaserPosition = { x, y, pressed ->
                                viewModel.updateLaserPosition(x, y, pressed)
                            },
                            onUpdatePointerSize = { viewModel.updatePointerSize(it) },
                            onToggleBlankMode = { viewModel.toggleScreenBlankMode(it) },
                            onToggleSlideshowF5 = { viewModel.toggleSlideshowF5() },
                            onOpenRemoteTrackpad = { viewModel.navigateTo(LinkScreen.REMOTE) },
                            onToggleConnection = { viewModel.toggleConnection() },
                            onRestoreSampleData = { viewModel.restoreSampleData() },
                            onNavigateToScreen = { viewModel.navigateTo(it) },
                            onDismissBanner = { viewModel.dismissBannerMessage() }
                        )
                    }

                    LinkScreen.MEDIA -> {
                        MediaScreen(
                            uiState = uiState,
                            onTogglePlayPause = { viewModel.togglePlayPause() },
                            onSkipPrevious = { viewModel.skipPreviousTrack() },
                            onSkipNext = { viewModel.skipNextTrack() },
                            onSeekTo = { viewModel.seekMediaTo(it) },
                            onVolumeChange = { viewModel.setMediaVolume(it) },
                            onToggleMute = { viewModel.toggleMediaMute() },
                            onSwitchSource = { name, sub -> viewModel.switchMediaSource(name, sub) },
                            onToggleConnection = { viewModel.toggleConnection() },
                            onRestoreSampleData = { viewModel.restoreSampleData() },
                            onNavigateToScreen = { viewModel.navigateTo(it) },
                            onDismissBanner = { viewModel.dismissBannerMessage() }
                        )
                    }

                    LinkScreen.CLIPBOARD -> {
                        ClipboardScreen(
                            uiState = uiState,
                            onUpdateDraft = { viewModel.updateClipboardDraft(it) },
                            onSendToLaptop = { viewModel.sendClipboardToLaptop() },
                            onPullFromLaptop = { viewModel.simulatePullFromLaptopClipboard() },
                            onToggleConnection = { viewModel.toggleConnection() },
                            onRestoreSampleData = { viewModel.restoreSampleData() },
                            onNavigateToScreen = { viewModel.navigateTo(it) },
                            onDismissBanner = { viewModel.dismissBannerMessage() }
                        )
                    }

                    LinkScreen.SECURITY -> {
                        ConnectionSecurityScreen(
                            uiState = uiState,
                            onUpdatePairingCode = { viewModel.updatePairingCodeInput(it) },
                            onToggleQrScanner = { viewModel.toggleQrScannerPreview() },
                            onSimulateQrPairSuccess = { viewModel.simulateQrPairingSuccess() },
                            onTogglePermission = { viewModel.togglePermission(it) },
                            onDisconnectRevoke = {
                                viewModel.setPrototypeStateMode(PrototypeStateMode.DISCONNECTED)
                            },
                            onToggleConnection = { viewModel.toggleConnection() },
                            onRestoreSampleData = { viewModel.restoreSampleData() },
                            onNavigateToScreen = { viewModel.navigateTo(it) },
                            onDismissBanner = { viewModel.dismissBannerMessage() }
                        )
                    }
                }
            }
        }
    }
}
