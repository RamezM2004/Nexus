# Link — Private Windows 11 Companion App

Link is a native Android companion application built with Kotlin, Jetpack Compose, and Material 3 for privately connecting a Samsung phone to a Windows 11 laptop. It delivers a high-precision, charcoal-and-cobalt device-management interface across six dedicated screens with complete interactive states (Connected, Connecting/Loading, Disconnected, Empty, and Error) and a modular repository interface ready for future Windows daemon integration.

## User Review & Critical Decisions

> [!IMPORTANT]
> All core scope, navigation, and state-testing preferences have been confirmed from your answers:

- **Confirmed Decision 1 — Pure Companion Scope (No AI or Cloud)**: We will build the **Link** companion application strictly per your specification without Firebase, cloud storage, analytics, Bluetooth permissions, or AI/chatbot features.
- **Confirmed Decision 2 — Home Dashboard Hub + Stack Navigation**: The **Home** screen acts as the central command hub with quick-launch cards to **File Transfer**, **Remote Control**, **Media**, **Clipboard**, and **Connection & Security**, supported by top-bar back navigation and system back handling.
- **Confirmed Decision 3 — Interactive State Switcher Bar**: A collapsible/pinned **Prototype State Switcher Bar** will be available across the app so you can instantly switch between `Connected`, `Connecting (Loading)`, `Disconnected`, `Empty`, and `Error` states in real time, in addition to natural interactive flows (e.g., tapping Connect/Disconnect, queuing transfers, or sending clipboard text).

---

## 1. Overview & Core Concept

- **What It Does**: Provides a unified, private control surface on Android to monitor laptop connection status, browse shared folders and transfer files, operate a responsive remote touchpad and keyboard, control desktop media playback (Windows Media Session / YouTube Music ready), sync clipboard text bi-directionally, and manage QR pairing and trusted-device permissions.
- **Target Audience / Persona**: Power users who want a fast, private, local-network bridge between their Samsung Galaxy phone and Windows 11 workstation without relying on third-party cloud relays.
- **Key Value**: Combines low-latency hardware ergonomics (one-handed touchpad, media transport, instant clipboard push) with transparent connection security and clear prototype honesty (explicitly distinguishing simulated UI states from live socket transport).

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Home Command Hub**:
   - Displays a hero **Windows 11 Laptop Status Card** (`Galaxy-Book-Pro / WIN11-WORKSTATION`, local IP `192.168.1.42`, Wi-Fi 6E link quality, latency badge, and battery state when connected) with a primary **Connect / Disconnect** action button and a quick link to **Connection & Security**.
   - Features a **2x2 Shortcut Grid** for **Files**, **Remote Control**, **Media**, and **Clipboard**, each showing live contextual subtitles (e.g., active transfers count, current media track, last clipboard snippet).
   - Lists **Recent File Transfers** with directional upload/download badges, file size, progress bars, and completion or error status.
2. **File Transfer**:
   - Displays shared laptop folders (`Desktop`, `Downloads`, `Documents`, `Pictures`, `Engineering_Projects`) with path breadcrumbs and item counts.
   - Provides clear **Upload Photo/File to Laptop** and **Download to Phone** actions (integrating Android's zero-permission Photo Picker / Document Picker or simulated file queuing).
   - Shows an active & completed transfer queue with directional indicators (`Upload ↑` / `Download ↓`), byte sizes, animated progress bars, retry actions on error, and an explicit prototype simulation notice.
3. **Remote Control**:
   - Features a large, responsive **Multi-Touch Trackpad Surface** with live cursor coordinate/delta feedback, ripple trail visual indicators, and sensitivity control.
   - Includes tactile **Left-Click**, **Middle/Scroll Wheel**, and **Right-Click** buttons, plus a dedicated vertical scroll strip and **Keyboard Input Bar** (with modifier toggles: `Win`, `Ctrl`, `Alt`, `Shift`, `Esc`, `Tab`, `Enter`) to send keystrokes.
   - Includes a compact **Quick Media Control Panel** at the bottom so users can pause/skip audio without leaving the trackpad view.
4. **Media**:
   - Shows a dedicated player layout styled for **Windows Media Session / YouTube Music** integration, including album artwork placeholder, track title, artist, source app badge (`YouTube Music — Chrome / Windows 11`), and seek scrubber with timestamps.
   - Offers tactile **Previous**, **Play/Pause**, **Next**, **Mute**, and **Master Volume Slider** controls, plus output device selector preview (`Realtek Audio` vs. `Headphones`).
5. **Clipboard**:
   - Provides a multi-line **Clipboard Composer** to type or paste text on the phone, with clear **Send to Laptop** and **Copy to Phone Clipboard** actions.
   - Displays a **Recent Clipboard Sync History** list showing direction (`Phone → Laptop` or `Laptop → Phone`), timestamp, character count, and one-tap re-copy/re-send buttons.
6. **Connection & Security**:
   - Features a **Device Pairing & QR Scanner** card with a stylized camera viewfinder frame, manual pairing code entry (`LINK-8492-W11`), and simulated QR scan trigger.
   - Displays **Trusted Device Details** (TLS 1.3 fingerprint `SHA-256: 9F:4A:...:E1`, local subnet restriction, last seen timestamp).
   - Provides granular **Permission Controls** (Allow File System Access, Allow Mouse & Keyboard Injection, Allow Media Session Control, Auto-Sync Clipboard) and a prominent **Revoke Trust / Disconnect** action.

### Visual Identity & Theme

- **Aesthetic Direction**: Utilitarian dark hardware companion—inspired by precision industrial tools and modern Windows 11 / One UI dark surfaces.
- **Color Palette & Mood**:
  - `--background`: Deep Charcoal (`#0E1014`)
  - `--surface`: Elevated Slate Charcoal (`#161920`)
  - `--surface-variant`: Card & Control Surface (`#1E232D`)
  - `--border`: Subtle Hairline Stroke (`#2A3140`)
  - `--primary`: Electric Cobalt Blue (`#3B82F6`)
  - `--primary-container`: Deep Cobalt Tint (`#172554`)
  - `--accent`: Cyan Telemetry Highlight (`#38BDF8`)
  - `--success`: Emerald Connected Indicator (`#10B981`)
  - `--warning`: Amber Transfer/Sync Indicator (`#F59E0B`)
  - `--error`: Coral Disconnected/Error Indicator (`#EF4444`)
- **Typography & Hierarchy**:
  - **Display & Headings**: `Space Grotesk` (bundled locally via `font-util`) for crisp technical headers, device names, and metric badges.
  - **Body & Controls**: `Plus Jakarta Sans` for high-legibility labels and descriptions.
  - **Monospace Telemetry**: `JetBrains Mono` for IP addresses, SHA-256 fingerprints, cursor coordinates, and file sizes.
- **Component Styling & Layout**:
  - Rounded `16.dp` and `20.dp` cards with `1.dp` subtle hairline borders (`#2A3140`) instead of heavy drop shadows.
  - Persistent top-or-bottom **Prototype State Switcher Bar** (`Connected` | `Loading` | `Disconnected` | `Empty` | `Error`) with single-tap pills so every screen state can be inspected immediately.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Stack Navigation from a Central Home Hub**
  - *Chosen Approach*: **Home** serves as the root destination with a top action button for **Connection & Security** and four prominent feature cards for **File Transfer**, **Remote Control**, **Media**, and **Clipboard**. Each child screen has a consistent top app bar with back navigation and a live connection status pill.
  - *Why*: Matches your preference for a Home dashboard hub and maximizes vertical screen real estate on the **Remote Control** touchpad and **File Transfer** browser without a permanent bottom tab bar getting in the way of thumb gestures.
  - *Alternatives Considered*: A 5-tab bottom navigation bar, which was passed over to keep the hub hierarchy clean and prevent accidental tab switches while using the bottom mouse buttons.
- **Decision 2: Clean Companion Repository Boundary (`LinkCompanionGateway`)**
  - *Chosen Approach*: All UI state flows through a single `LinkViewModel` backed by a modular `LinkCompanionGateway` interface and a `PrototypeLinkGateway` implementation with representative mock data and state simulation.
  - *Why*: Ensures zero hardcoded fake claims in the UI while making it straightforward to swap in a real LAN/WebSocket or gRPC Windows 11 daemon implementation later without refactoring any Compose screens.
- **Decision 3: Zero External Services or Unused Libraries**
  - *Chosen Approach*: Remove/comment out unused template dependencies (Firebase, Room, Retrofit) in `build.gradle.kts` and request zero Bluetooth or cloud permissions.
  - *Why*: Strictly honors the privacy and specification constraints while keeping APK size small and builds fast.

---

## 4. Technical Architecture & Data Strategy *(Technical Reference)*

### Architecture & Component Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       MainActivity & LinkTheme                          │
│         (Charcoal #0E1014, Cobalt #3B82F6, Bundled Local Fonts)         │
└───────────────────────────────────┬─────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    LinkAppShell + StateSwitcherBar                      │
│   [Connected]  [Connecting]  [Disconnected]  [Empty]  [Error]           │
└───────────────────────────────────┬─────────────────────────────────────┘
                                    │
        ┌───────────────────────────┼───────────────────────────┐
        ▼                           ▼                           ▼
┌───────────────────┐     ┌───────────────────┐     ┌─────────────────────┐
│    HomeScreen     │     │ Feature Screens   │     │ ConnectionSecurity  │
│  (Dashboard Hub)  │────▶│ • FileTransfer    │     │       Screen        │
│ • Laptop Status   │     │ • RemoteControl   │     │ • QR Scanner Frame  │
│ • 4 Feature Cards │     │ • MediaScreen     │     │ • Trusted Device    │
│ • Recent Transfers│     │ • ClipboardScreen │     │ • Permission Toggles│
└─────────┬─────────┘     └─────────┬─────────┘     └──────────┬──────────┘
          │                         │                          │
          └─────────────────────────┼──────────────────────────┘
                                    │ UI Events & StateFlow
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                             LinkViewModel                               │
│        Holds LinkUiState (ConnectionState, Transfers, Media, etc.)      │
└───────────────────────────────────┬─────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│               LinkCompanionGateway (Modular Interface)                  │
│      Implemented by PrototypeLinkGateway (Representative Mock Data)     │
└─────────────────────────────────────────────────────────────────────────┘
```

### Data Model & State

- **`PrototypeMode`**: `CONNECTED`, `LOADING`, `DISCONNECTED`, `EMPTY`, `ERROR` — controls the global or screen-level state preview via the interactive State Switcher Bar.
- **`LaptopConnectionInfo`**: Device name (`WIN11-WORKSTATION`), model (`Galaxy Book4 Pro / Windows 11 Pro`), local IP, latency (`4 ms`), Wi-Fi band, battery percentage, TLS fingerprint, and pairing timestamp.
- **`SharedFolder` & `TransferItem`**: Folder metadata (name, path, item count, size) and file transfer records (id, fileName, fileSize, direction `UPLOAD` / `DOWNLOAD`, progress `0f..1f`, status `IN_PROGRESS` / `COMPLETED` / `FAILED`).
- **`RemoteControlState`**: Last pointer coordinate/delta, sensitivity multiplier, scroll velocity, active keyboard modifiers (`Win`, `Ctrl`, `Alt`, `Shift`), and last dispatched input command log (showing clearly what would be sent to the Windows companion).
- **`MediaSessionState`**: Track title, artist, album, source app (`YouTube Music` / `Windows Media Session`), playback state (`Playing` / `Paused`), current position, duration, volume (`0f..1f`), and muted flag.
- **`ClipboardEntry`**: ID, text content, direction (`PHONE_TO_PC` / `PC_TO_PHONE`), timestamp, and sync status.
- **`SecurityPermissions`**: Individual booleans for File Access, Input Injection, Media Control, and Clipboard Auto-Sync.

### Interactive Component & State Mapping

- **State Switcher Bar**: Tapping any mode chip (`Connected`, `Loading`, `Disconnected`, `Empty`, `Error`) immediately updates `LinkViewModel` so the current screen renders the corresponding state (e.g., shimmer/spinner on `Loading`, offline banner with reconnect CTA on `Disconnected`, zero-state illustration on `Empty`, and diagnostic error card with `Retry` on `Error`).
- **Home Screen**: Tapping **Connect / Disconnect** toggles connection state; tapping any of the 4 shortcut cards or the **Security** badge navigates to that screen; tapping a recent transfer navigates to **File Transfer**.
- **File Transfer Screen**: Selecting a shared folder filters its contents; tapping **Upload to Laptop** or **Download to Phone** adds a simulated transfer with animated progress from `0%` to `100%` (or allows retrying failed items).
- **Remote Control Screen**: Dragging across the touchpad canvas updates live pointer coordinates and visual touch trails; tapping Left/Right click, scroll buttons, keyboard keys, or embedded media transport controls logs and displays the exact companion command payload.
- **Media Screen**: Tapping Play/Pause, Previous/Next, Mute, or dragging the seek/volume sliders updates the active media session state in real time.
- **Clipboard Screen**: Typing text and tapping **Send to Laptop** appends a new `Phone → Laptop` entry to history; tapping **Copy** copies text to the Android system `ClipboardManager` with visual confirmation.
- **Connection & Security Screen**: Tapping **Simulate QR Scan** or entering a pairing code pairs the device; toggling permission switches updates trusted capabilities; tapping **Disconnect / Revoke** transitions the app to the `Disconnected` state.
