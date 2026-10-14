package com.retrobit.emulator

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.retrobit.emulator.data.Rom
import com.retrobit.emulator.native.NativeCore
import com.retrobit.emulator.ui.LibraryViewModel
import com.retrobit.emulator.ui.theme.RetroBitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RetroBitTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    RetroBitAppNav()
                }
            }
        }
    }
}

@Composable
fun RetroBitAppNav() {
    val nav = rememberNavController()
    val vm: LibraryViewModel = viewModel()
    NavHost(navController = nav, startDestination = "home") {
        composable("home") { HomeScreen(nav, vm) }
        composable("library") { LibraryScreen(nav, vm) }
        composable(
            "player/{romId}",
            arguments = listOf(navArgument("romId") { type = NavType.StringType })
        ) { back ->
            val id = back.arguments?.getString("romId") ?: ""
            PlayerScreen(nav, vm, id)
        }
        composable("settings") { SettingsScreen(nav) }
    }
}

@Composable
fun HomeScreen(nav: androidx.navigation.NavController, vm: LibraryViewModel) {
    val roms by vm.roms.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("RetroBit Emulator", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFFE60012))
        Text("Nintendo Switch emulator for Android", color = Color.Gray)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { nav.navigate("library") }) { Text("Library (${roms.size})") }
            OutlinedButton(onClick = { nav.navigate("settings") }) { Text("Settings") }
        }
        Spacer(Modifier.height(16.dp))
        Text("Core status: ${if (NativeCore.isAvailable()) "Native core loaded" else "No native core (external emulator required)"}",
            color = if (NativeCore.isAvailable()) Color(0xFF3DDC97) else Color(0xFFFFB020))
        Spacer(Modifier.height(16.dp))
        Text("Supported formats: .nsp, .xci, .nca, .nro", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(8.dp))
        Text("Add ROMs from the Library tab. Tap a ROM to launch playback.", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun LibraryScreen(nav: androidx.navigation.NavController, vm: LibraryViewModel) {
    val context = LocalContext.current
    val roms by vm.roms.collectAsState()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val name = it.lastPathSegment ?: "rom"
            val size = try { context.contentResolver.openFileDescriptor(it, "r")?.statSize ?: 0L } catch (e: Exception) { 0L }
            val system = when {
                name.endsWith(".nsp", true) || name.endsWith(".xci", true) || name.endsWith(".nca", true) || name.endsWith(".nro", true) -> "switch"
                else -> "switch"
            }
            vm.addRom(name, it, system, size)
        }
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Library", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Button(onClick = { launcher.launch(arrayOf("*/*")) }) { Text("Add ROM") }
        }
        Spacer(Modifier.height(12.dp))
        if (roms.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No ROMs yet. Tap \"Add ROM\" to pick a .nsp/.xci file.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(roms, key = { it.id }) { rom ->
                    RomCard(rom, onOpen = { nav.navigate("player/${rom.id}") }, onDelete = { vm.remove(rom.id) })
                }
            }
        }
    }
}

@Composable
fun RomCard(rom: Rom, onOpen: () -> Unit, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth(), onClick = onOpen) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(rom.name, fontWeight = FontWeight.SemiBold)
                Text("${rom.system.uppercase()} · ${rom.sizeBytes / 1024 / 1024} MB", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = onDelete) { Text("Delete", color = Color(0xFFFF4D5A)) }
        }
    }
}

@Composable
fun PlayerScreen(nav: androidx.navigation.NavController, vm: LibraryViewModel, romId: String) {
    val roms by vm.roms.collectAsState()
    val rom = roms.find { it.id == romId }
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { nav.popBackStack() }) { Text("← Back") }
            Text(rom?.name ?: "Unknown ROM", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Box(
            Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text("Emulation surface", color = Color.Gray)
        }
        Spacer(Modifier.height(12.dp))
        if (rom != null) {
            Text("URI: ${rom.uri}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                // Try external emulator cores installed on device
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(rom?.uri), "*/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                try { context.startActivity(intent) } catch (e: Exception) {
                    // no handler
                }
            }) { Text("Launch External Core") }
            OutlinedButton(onClick = { nav.popBackStack() }) { Text("Stop") }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Native core available: ${NativeCore.isAvailable()}. " +
            "For real Switch emulation install Skyline/Sudachi/Eden and use 'Launch External Core', " +
            "or bundle a libretro core as libretrobit.so.",
            color = Color.Gray, style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun SettingsScreen(nav: androidx.navigation.NavController) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = { nav.popBackStack() }) { Text("← Back") }
        Text("Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text("Resolution scale: 1x (default)", color = Color.Gray)
        Text("Volume: 80%", color = Color.Gray)
        Text("Show FPS: on", color = Color.Gray)
        Spacer(Modifier.height(12.dp))
        Text("Core bridge: native libretrobit.so", color = Color.Gray)
    }
}
