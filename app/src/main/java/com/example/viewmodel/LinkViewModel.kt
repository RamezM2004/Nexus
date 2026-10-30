package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LinkCompanionGateway
import com.example.data.PrototypeLinkGateway
import com.example.model.ClipboardDirection
import com.example.model.ClipboardEntry
import com.example.model.FileTransferItem
import com.example.model.FolderFileItem
import com.example.model.LinkScreen
import com.example.model.LinkUiState
import com.example.model.PointerVisualMode
import com.example.model.PrototypeStateMode
import com.example.model.ScreenBlankMode
import com.example.model.TransferDirection
import com.example.model.TransferStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class LinkViewModel(
    private val gateway: LinkCompanionGateway = PrototypeLinkGateway()
) : ViewModel() {

    private val defaultFolders = gateway.getDefaultSharedFolders()

    private val _uiState = MutableStateFlow(
        LinkUiState(
            currentScreen = LinkScreen.HOME,
            stateMode = PrototypeStateMode.CONNECTED,
            laptopInfo = gateway.getDefaultLaptopInfo(),
            sharedFolders = defaultFolders,
            selectedFolderId = defaultFolders.first().id,
            transfers = gateway.getDefaultTransfers(),
            mediaState = gateway.getDefaultMediaSession(),
            clipboardHistory = gateway.getDefaultClipboardHistory()
        )
    )
    val uiState: StateFlow<LinkUiState> = _uiState.asStateFlow()

    private val activeTransferJobs = mutableMapOf<String, Job>()
    private var presentationTimerJob: Job? = null

    init {
        startPresentationTimerLoop()
    }

    private fun startPresentationTimerLoop() {
        presentationTimerJob?.cancel()
        presentationTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { state ->
                    if (state.presentationState.isTimerRunning && state.stateMode == PrototypeStateMode.CONNECTED) {
                        state.copy(
                            presentationState = state.presentationState.copy(
                                elapsedSeconds = state.presentationState.elapsedSeconds + 1
                            )
                        )
                    } else {
                        state
                    }
                }
            }
        }
    }

    // --- Navigation ---
    fun navigateTo(screen: LinkScreen) {
        _uiState.update { state ->
            if (state.currentScreen == screen) return@update state
            val updatedStack = if (screen == LinkScreen.HOME) {
                emptyList()
            } else {
                state.backStack + state.currentScreen
            }
            state.copy(
                currentScreen = screen,
                backStack = updatedStack
            )
        }
    }

    fun navigateBack(): Boolean {
        val current = _uiState.value
        if (current.backStack.isEmpty()) {
            if (current.currentScreen != LinkScreen.HOME) {
                _uiState.update { it.copy(currentScreen = LinkScreen.HOME) }
                return true
            }
            return false
        }
        val previous = current.backStack.last()
        _uiState.update {
            it.copy(
                currentScreen = previous,
                backStack = it.backStack.dropLast(1)
            )
        }
        return true
    }

    // --- Theme Color Studio ---
    fun toggleThemeStudio(open: Boolean? = null) {
        _uiState.update { state ->
            val target = open ?: !state.themeConfig.isThemeStudioOpen
            state.copy(themeConfig = state.themeConfig.copy(isThemeStudioOpen = target))
        }
    }

    fun selectThemePreset(presetId: String) {
        _uiState.update { state ->
            state.copy(
                themeConfig = state.themeConfig.copy(
                    selectedPresetId = presetId,
                    useCustomHues = false
                )
            )
        }
    }

    fun updateCustomPrimaryHue(hue: Float) {
        _uiState.update { state ->
            state.copy(
                themeConfig = state.themeConfig.copy(
                    useCustomHues = true,
                    customPrimaryHue = hue.coerceIn(0f, 360f)
                )
            )
        }
    }

    fun updateCustomSecondaryHue(hue: Float) {
        _uiState.update { state ->
            state.copy(
                themeConfig = state.themeConfig.copy(
                    useCustomHues = true,
                    customSecondaryHue = hue.coerceIn(0f, 360f)
                )
            )
        }
    }

    fun toggleDeepOledDark() {
        _uiState.update { state ->
            state.copy(
                themeConfig = state.themeConfig.copy(
                    deepOledDark = !state.themeConfig.deepOledDark
                )
            )
        }
    }

    // --- Interactive Prototype State Switcher ---
    fun setPrototypeStateMode(mode: PrototypeStateMode) {
        _uiState.update { state ->
            when (mode) {
                PrototypeStateMode.CONNECTED -> state.copy(
                    stateMode = PrototypeStateMode.CONNECTED,
                    sharedFolders = if (state.sharedFolders.isEmpty()) defaultFolders else state.sharedFolders,
                    transfers = if (state.transfers.isEmpty() || state.stateMode == PrototypeStateMode.ERROR) {
                        gateway.getDefaultTransfers()
                    } else {
                        state.transfers
                    },
                    clipboardHistory = if (state.clipboardHistory.isEmpty()) {
                        gateway.getDefaultClipboardHistory()
                    } else {
                        state.clipboardHistory
                    },
                    mediaState = state.mediaState.copy(isAvailable = true),
                    statusBannerMessage = "Connected to ${state.laptopInfo.deviceName} (Simulated LAN)"
                )

                PrototypeStateMode.LOADING -> state.copy(
                    stateMode = PrototypeStateMode.LOADING,
                    statusBannerMessage = "Negotiating TLS 1.3 handshake with ${state.laptopInfo.ipAddress}…"
                )

                PrototypeStateMode.DISCONNECTED -> state.copy(
                    stateMode = PrototypeStateMode.DISCONNECTED,
                    statusBannerMessage = "Windows 11 companion is offline or disconnected"
                )

                PrototypeStateMode.EMPTY -> state.copy(
                    stateMode = PrototypeStateMode.EMPTY,
                    transfers = emptyList(),
                    clipboardHistory = emptyList(),
                    mediaState = state.mediaState.copy(isAvailable = false, isPlaying = false),
                    statusBannerMessage = "Empty state active — no recent transfers, media, or clipboard items"
                )

                PrototypeStateMode.ERROR -> state.copy(
                    stateMode = PrototypeStateMode.ERROR,
                    transfers = gateway.getErrorStateTransfers(),
                    statusBannerMessage = "Companion socket error: Windows firewall or port 9443 unreachable"
                )
            }
        }
    }

    fun toggleConnection() {
        val currentMode = _uiState.value.stateMode
        if (currentMode == PrototypeStateMode.CONNECTED || currentMode == PrototypeStateMode.EMPTY) {
            setPrototypeStateMode(PrototypeStateMode.DISCONNECTED)
        } else {
            viewModelScope.launch {
                setPrototypeStateMode(PrototypeStateMode.LOADING)
                delay(750)
                setPrototypeStateMode(PrototypeStateMode.CONNECTED)
            }
        }
    }

    fun restoreSampleData() {
        setPrototypeStateMode(PrototypeStateMode.CONNECTED)
    }

    fun dismissBannerMessage() {
        _uiState.update { it.copy(statusBannerMessage = null) }
    }

    // --- File Transfer Actions ---
    fun selectFolder(folderId: String) {
        _uiState.update { it.copy(selectedFolderId = folderId) }
    }

    fun queueSimulatedUpload(fileName: String, fileSizeLabel: String) {
        val state = _uiState.value
        val targetFolder = state.sharedFolders.find { it.id == state.selectedFolderId }
            ?: defaultFolders.first()
        val id = "tx_up_${System.currentTimeMillis()}"
        val newItem = FileTransferItem(
            id = id,
            fileName = fileName,
            folderPath = targetFolder.windowsPath,
            fileSizeLabel = fileSizeLabel,
            direction = TransferDirection.UPLOAD_TO_PC,
            progress = 0.08f,
            status = TransferStatus.IN_PROGRESS,
            timestampLabel = "Uploading • 64 MB/s",
            speedLabel = "64 MB/s"
        )
        _uiState.update {
            it.copy(
                stateMode = if (it.stateMode == PrototypeStateMode.EMPTY) PrototypeStateMode.CONNECTED else it.stateMode,
                transfers = listOf(newItem) + it.transfers,
                statusBannerMessage = "Queued upload to ${targetFolder.name}: $fileName"
            )
        }
        animateTransferToCompletion(id)
    }

    fun queueFolderFileDownload(file: FolderFileItem) {
        val id = "tx_dl_${System.currentTimeMillis()}"
        val newItem = FileTransferItem(
            id = id,
            fileName = file.name,
            folderPath = "Phone / Download / Link",
            fileSizeLabel = file.sizeLabel,
            direction = TransferDirection.DOWNLOAD_TO_PHONE,
            progress = 0.12f,
            status = TransferStatus.IN_PROGRESS,
            timestampLabel = "Downloading • 95 MB/s",
            speedLabel = "95 MB/s"
        )
        _uiState.update {
            it.copy(
                stateMode = if (it.stateMode == PrototypeStateMode.EMPTY) PrototypeStateMode.CONNECTED else it.stateMode,
                transfers = listOf(newItem) + it.transfers,
                statusBannerMessage = "Downloading ${file.name} to phone storage (Simulated)"
            )
        }
        animateTransferToCompletion(id)
    }

    fun retryFailedTransfers() {
        val failedIds = _uiState.value.transfers
            .filter { it.status == TransferStatus.FAILED || it.status == TransferStatus.IN_PROGRESS }
            .map { it.id }

        _uiState.update { state ->
            state.copy(
                stateMode = PrototypeStateMode.CONNECTED,
                transfers = state.transfers.map { item ->
                    if (item.id in failedIds) {
                        item.copy(
                            status = TransferStatus.IN_PROGRESS,
                            progress = 0.2f,
                            errorReason = null,
                            timestampLabel = "Retrying transfer • 78 MB/s"
                        )
                    } else {
                        item
                    }
                },
                statusBannerMessage = "Retrying interrupted file transfers…"
            )
        }
        failedIds.forEach { animateTransferToCompletion(it) }
    }

    private fun animateTransferToCompletion(transferId: String) {
        activeTransferJobs[transferId]?.cancel()
        activeTransferJobs[transferId] = viewModelScope.launch {
            for (step in 1..6) {
                delay(280)
                _uiState.update { state ->
                    state.copy(
                        transfers = state.transfers.map { item ->
                            if (item.id == transferId && item.status == TransferStatus.IN_PROGRESS) {
                                val nextProgress = (item.progress + 0.16f).coerceAtMost(1.0f)
                                if (nextProgress >= 1.0f) {
                                    item.copy(
                                        progress = 1.0f,
                                        status = TransferStatus.COMPLETED,
                                        timestampLabel = "Completed • Just now",
                                        speedLabel = null
                                    )
                                } else {
                                    item.copy(
                                        progress = nextProgress,
                                        timestampLabel = "${(nextProgress * 100).roundToInt()}% • ${item.speedLabel ?: "75 MB/s"}"
                                    )
                                }
                            } else {
                                item
                            }
                        }
                    )
                }
            }
        }
    }

    // --- Remote Control Actions ---
    fun toggleExpandedTrackpad() {
        _uiState.update { state ->
            state.copy(
                remoteState = state.remoteState.copy(
                    isExpandedTrackpad = !state.remoteState.isExpandedTrackpad
                )
            )
        }
    }

    fun onTrackpadMove(deltaX: Float, deltaY: Float) {
        _uiState.update { state ->
            val sens = state.remoteState.sensitivity
            val scaledDx = (deltaX * sens)
            val scaledDy = (deltaY * sens)
            val newX = (state.remoteState.cursorX + scaledDx).coerceIn(0f, 3840f)
            val newY = (state.remoteState.cursorY + scaledDy).coerceIn(0f, 2160f)
            val cmd = "MOUSE_MOVE dx=${scaledDx.roundToInt()} dy=${scaledDy.roundToInt()} -> (${newX.roundToInt()}, ${newY.roundToInt()})"
            state.copy(
                remoteState = state.remoteState.copy(
                    cursorX = newX,
                    cursorY = newY,
                    lastDeltaX = scaledDx,
                    lastDeltaY = scaledDy,
                    lastDispatchedCommand = cmd
                )
            )
        }
    }

    fun onMouseButtonClick(buttonLabel: String) {
        recordRemoteCommand("MOUSE_CLICK [$buttonLabel] @ (${_uiState.value.remoteState.cursorX.roundToInt()}, ${_uiState.value.remoteState.cursorY.roundToInt()})")
    }

    fun onVerticalScroll(deltaY: Float) {
        val direction = if (deltaY < 0) "SCROLL_UP" else "SCROLL_DOWN"
        recordRemoteCommand("WHEEL_SCROLL [$direction] ticks=${(deltaY / 8f).roundToInt()}")
    }

    fun onSensitivityChange(value: Float) {
        _uiState.update { state ->
            state.copy(
                remoteState = state.remoteState.copy(
                    sensitivity = value.coerceIn(0.5f, 3.0f)
                )
            )
        }
    }

    fun toggleKeyboardModifier(modifierKey: String) {
        _uiState.update { state ->
            val current = state.remoteState.activeModifiers.toMutableSet()
            if (modifierKey in current) current.remove(modifierKey) else current.add(modifierKey)
            state.copy(
                remoteState = state.remoteState.copy(
                    activeModifiers = current,
                    lastDispatchedCommand = if (current.isEmpty()) {
                        "MODIFIERS_CLEARED"
                    } else {
                        "MODIFIERS_HELD [${current.joinToString(" + ")}]"
                    }
                )
            )
        }
    }

    fun sendQuickShortcut(shortcutLabel: String) {
        val activeMods = _uiState.value.remoteState.activeModifiers
        val prefix = if (activeMods.isNotEmpty()) activeMods.joinToString("+") + "+" else ""
        recordRemoteCommand("KEY_SHORTCUT [$prefix$shortcutLabel]")
    }

    fun updateKeyboardDraft(text: String) {
        _uiState.update { state ->
            state.copy(remoteState = state.remoteState.copy(keyboardDraft = text))
        }
    }

    fun sendKeyboardDraft() {
        val draft = _uiState.value.remoteState.keyboardDraft.trim()
        if (draft.isEmpty()) return
        recordRemoteCommand("KEY_STRING \"${draft.take(36)}\" (${draft.length} chars)")
        _uiState.update { state ->
            state.copy(remoteState = state.remoteState.copy(keyboardDraft = ""))
        }
    }

    private fun recordRemoteCommand(command: String) {
        _uiState.update { state ->
            val updatedHistory = (listOf(command) + state.remoteState.commandHistory).take(8)
            state.copy(
                remoteState = state.remoteState.copy(
                    lastDispatchedCommand = command,
                    commandHistory = updatedHistory
                )
            )
        }
    }

    // --- Presentation & Laser Pointer Actions ---
    fun nextSlide() {
        _uiState.update { state ->
            val p = state.presentationState
            val next = (p.currentSlide + 1).coerceAtMost(p.totalSlides)
            state.copy(
                presentationState = p.copy(
                    currentSlide = next,
                    blankMode = ScreenBlankMode.NONE,
                    lastPresentationCommand = "KEY_PRESS [NEXT_SLIDE / RightArrow] -> Slide $next/${p.totalSlides}"
                )
            )
        }
    }

    fun previousSlide() {
        _uiState.update { state ->
            val p = state.presentationState
            val prev = (p.currentSlide - 1).coerceAtLeast(1)
            state.copy(
                presentationState = p.copy(
                    currentSlide = prev,
                    blankMode = ScreenBlankMode.NONE,
                    lastPresentationCommand = "KEY_PRESS [PREV_SLIDE / LeftArrow] -> Slide $prev/${p.totalSlides}"
                )
            )
        }
    }

    fun togglePresentationTimer() {
        _uiState.update { state ->
            val p = state.presentationState
            state.copy(
                presentationState = p.copy(
                    isTimerRunning = !p.isTimerRunning,
                    lastPresentationCommand = if (!p.isTimerRunning) "TIMER_RESUMED" else "TIMER_PAUSED"
                )
            )
        }
    }

    fun resetPresentationTimer() {
        _uiState.update { state ->
            val p = state.presentationState
            state.copy(
                presentationState = p.copy(
                    elapsedSeconds = 0,
                    isTimerRunning = true,
                    lastPresentationCommand = "TIMER_RESET -> 00:00"
                )
            )
        }
    }

    fun selectPointerMode(mode: PointerVisualMode) {
        _uiState.update { state ->
            val p = state.presentationState
            state.copy(
                presentationState = p.copy(
                    pointerMode = mode,
                    lastPresentationCommand = "POINTER_MODE [${mode.label.uppercase()}]"
                )
            )
        }
    }

    fun updateLaserPosition(normX: Float, normY: Float, isPressed: Boolean) {
        val clampedX = normX.coerceIn(0.05f, 0.95f)
        val clampedY = normY.coerceIn(0.05f, 0.95f)
        val screenX = (clampedX * 1920).roundToInt()
        val screenY = (clampedY * 1080).roundToInt()
        _uiState.update { state ->
            val p = state.presentationState
            state.copy(
                presentationState = p.copy(
                    pointerNormX = clampedX,
                    pointerNormY = clampedY,
                    isLaserPressed = isPressed,
                    lastPresentationCommand = "${p.pointerMode.name} @ ($screenX, $screenY) 1080p Stage"
                )
            )
        }
    }

    fun updatePointerSize(size: Float) {
        _uiState.update { state ->
            state.copy(
                presentationState = state.presentationState.copy(
                    pointerSize = size.coerceIn(0.6f, 2.5f)
                )
            )
        }
    }

    fun toggleScreenBlankMode(mode: ScreenBlankMode) {
        _uiState.update { state ->
            val p = state.presentationState
            val nextMode = if (p.blankMode == mode) ScreenBlankMode.NONE else mode
            state.copy(
                presentationState = p.copy(
                    blankMode = nextMode,
                    lastPresentationCommand = "SCREEN_BLANK [${nextMode.label}]"
                )
            )
        }
    }

    fun toggleSlideshowF5() {
        _uiState.update { state ->
            val p = state.presentationState
            val nextActive = !p.isSlideshowActive
            state.copy(
                presentationState = p.copy(
                    isSlideshowActive = nextActive,
                    lastPresentationCommand = if (nextActive) "KEY_SHORTCUT [F5 -> Start Presentation]" else "KEY_SHORTCUT [Esc -> End Presentation]"
                )
            )
        }
    }

    // --- Media Controls ---
    private val sampleTracks = listOf(
        Triple("Midnight City (Instrumental Mix)", "M83 • Hurry Up, We're Dreaming", 243),
        Triple("Veridis Quo", "Daft Punk • Discovery", 345),
        Triple("Time — Hans Zimmer", "Inception (Original Motion Picture Soundtrack)", 275),
        Triple("Nightcall", "Kavinsky • OutRun", 258)
    )
    private var currentTrackIndex = 0

    fun togglePlayPause() {
        _uiState.update { state ->
            val nextPlaying = !state.mediaState.isPlaying
            state.copy(
                mediaState = state.mediaState.copy(
                    isAvailable = true,
                    isPlaying = nextPlaying
                )
            )
        }
    }

    fun skipNextTrack() {
        currentTrackIndex = (currentTrackIndex + 1) % sampleTracks.size
        val (title, artist, duration) = sampleTracks[currentTrackIndex]
        _uiState.update { state ->
            state.copy(
                mediaState = state.mediaState.copy(
                    isAvailable = true,
                    trackTitle = title,
                    artistName = artist,
                    positionSeconds = 0,
                    durationSeconds = duration,
                    isPlaying = true
                )
            )
        }
    }

    fun skipPreviousTrack() {
        currentTrackIndex = if (currentTrackIndex - 1 < 0) sampleTracks.lastIndex else currentTrackIndex - 1
        val (title, artist, duration) = sampleTracks[currentTrackIndex]
        _uiState.update { state ->
            state.copy(
                mediaState = state.mediaState.copy(
                    isAvailable = true,
                    trackTitle = title,
                    artistName = artist,
                    positionSeconds = 0,
                    durationSeconds = duration,
                    isPlaying = true
                )
            )
        }
    }

    fun seekMediaTo(seconds: Int) {
        _uiState.update { state ->
            state.copy(
                mediaState = state.mediaState.copy(
                    positionSeconds = seconds.coerceIn(0, state.mediaState.durationSeconds)
                )
            )
        }
    }

    fun setMediaVolume(level: Float) {
        _uiState.update { state ->
            state.copy(
                mediaState = state.mediaState.copy(
                    volumeLevel = level.coerceIn(0f, 1f),
                    isMuted = level <= 0.01f
                )
            )
        }
    }

    fun toggleMediaMute() {
        _uiState.update { state ->
            state.copy(
                mediaState = state.mediaState.copy(
                    isMuted = !state.mediaState.isMuted
                )
            )
        }
    }

    fun switchMediaSource(sourceName: String, subtitle: String) {
        _uiState.update { state ->
            state.copy(
                mediaState = state.mediaState.copy(
                    isAvailable = true,
                    sourceApp = sourceName,
                    sourceSubtitle = subtitle
                )
            )
        }
    }

    // --- Clipboard Sync ---
    fun updateClipboardDraft(text: String) {
        _uiState.update { it.copy(clipboardDraft = text) }
    }

    fun sendClipboardToLaptop() {
        val text = _uiState.value.clipboardDraft.trim()
        if (text.isEmpty()) return
        val newEntry = ClipboardEntry(
            id = "clip_${System.currentTimeMillis()}",
            content = text,
            direction = ClipboardDirection.PHONE_TO_PC,
            timestampLabel = "Sent just now",
            charCount = text.length,
            isDelivered = _uiState.value.stateMode == PrototypeStateMode.CONNECTED
        )
        _uiState.update { state ->
            state.copy(
                stateMode = if (state.stateMode == PrototypeStateMode.EMPTY) PrototypeStateMode.CONNECTED else state.stateMode,
                clipboardDraft = "",
                clipboardHistory = listOf(newEntry) + state.clipboardHistory,
                statusBannerMessage = "Pushed ${text.length} characters to Windows 11 clipboard (Simulated)"
            )
        }
    }

    fun simulatePullFromLaptopClipboard() {
        val sampleSnippet = "WIN11_CLIPBOARD_SYNC: C:\\Users\\Ramez\\Projects\\build_output_release.apk"
        val newEntry = ClipboardEntry(
            id = "clip_pc_${System.currentTimeMillis()}",
            content = sampleSnippet,
            direction = ClipboardDirection.PC_TO_PHONE,
            timestampLabel = "Received just now",
            charCount = sampleSnippet.length,
            isDelivered = true
        )
        _uiState.update { state ->
            state.copy(
                stateMode = if (state.stateMode == PrototypeStateMode.EMPTY) PrototypeStateMode.CONNECTED else state.stateMode,
                clipboardDraft = sampleSnippet,
                clipboardHistory = listOf(newEntry) + state.clipboardHistory,
                statusBannerMessage = "Pulled latest laptop clipboard into composer"
            )
        }
    }

    // --- Connection & Security ---
    fun updatePairingCodeInput(code: String) {
        _uiState.update { it.copy(pairingCodeInput = code) }
    }

    fun toggleQrScannerPreview() {
        _uiState.update { it.copy(isQrScannerActive = !it.isQrScannerActive) }
    }

    fun simulateQrPairingSuccess() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    stateMode = PrototypeStateMode.LOADING,
                    isQrScannerActive = false,
                    lastPairingEvent = "Verifying QR key exchange with WIN11-WORKSTATION…"
                )
            }
            delay(700)
            _uiState.update {
                it.copy(
                    stateMode = PrototypeStateMode.CONNECTED,
                    lastPairingEvent = "QR Paired • TLS 1.3 pin verified at 192.168.1.42:9443",
                    statusBannerMessage = "Successfully paired with ${it.laptopInfo.deviceName}"
                )
            }
        }
    }

    fun togglePermission(permissionKey: String) {
        _uiState.update { state ->
            val p = state.permissions
            val updated = when (permissionKey) {
                "files" -> p.copy(allowFileSystemAccess = !p.allowFileSystemAccess)
                "input" -> p.copy(allowMouseKeyboardControl = !p.allowMouseKeyboardControl)
                "media" -> p.copy(allowMediaSessionControl = !p.allowMediaSessionControl)
                "clipboard" -> p.copy(autoSyncClipboard = !p.autoSyncClipboard)
                else -> p
            }
            state.copy(permissions = updated)
        }
    }
}
