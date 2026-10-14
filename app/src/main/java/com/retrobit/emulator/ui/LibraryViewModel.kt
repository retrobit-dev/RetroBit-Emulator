package com.retrobit.emulator.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.retrobit.emulator.data.Rom
import com.retrobit.emulator.data.RomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class LibraryViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = RomRepository(app)
    private val _roms = MutableStateFlow(repo.load())
    val roms: StateFlow<List<Rom>> = _roms

    fun addRom(name: String, uri: Uri, system: String, size: Long) {
        viewModelScope.launch {
            repo.add(Rom(UUID.randomUUID().toString(), name, uri.toString(), system, size))
            _roms.value = repo.load()
        }
    }

    fun remove(id: String) {
        viewModelScope.launch {
            repo.remove(id)
            _roms.value = repo.load()
        }
    }
}
