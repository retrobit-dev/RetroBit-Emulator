# RetroBit Emulator (Android)

Native Android (Kotlin + Jetpack Compose) application acting as a frontend/emulator
host for **Nintendo Switch** games (.nsp, .xci, .nca, .nro).

## Features
- Native Android UI (Jetpack Compose, Material 3)
- ROM library with Storage Access Framework (persistent URI permissions)
- Save/remove ROMs (persisted via SharedPreferences)
- Player screen with emulation surface placeholder
- Launch external cores (Skyline / Sudachi / Eden / Yuzu) via Intent
- JNI bridge stub (`libretrobit.so`) for bundling a real core
- Settings screen

## Build
Open in Android Studio (Hedgehog or newer), sync Gradle, then Run on a device/emulator.

Requirements:
- Android Studio
- JDK 17
- Android SDK (API 24+)
- NDK + CMake (for the native module)

## Real emulation
This project ships a JNI stub. To get actual Switch emulation, either:
1. Install a Switch emulator on the device (Skyline, Sudachi, Eden) and use "Launch External Core", or
2. Bundle a libretro/standalone core as `libretrobit.so` implementing the JNI functions in `NativeCore`.
