package com.retrobit.emulator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFFE60012),
    secondary = Color(0xFF4A90E2),
    background = Color(0xFF0B0D12),
    surface = Color(0xFF171B26),
    onBackground = Color(0xFFE8ECF4),
    onSurface = Color(0xFFE8ECF4),
)

@Composable
fun RetroBitTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, content = content)
}
