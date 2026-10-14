package com.retrobit.emulator.native

object NativeCore {
    private var loaded = false
    private var available = false

    init {
        try {
            System.loadLibrary("retrobit")
            loaded = true
            available = true
        } catch (e: UnsatisfiedLinkError) {
            loaded = false
            available = false
        }
    }

    fun isAvailable(): Boolean = available

    // JNI stubs (implemented in C++ if a core is bundled)
    external fun initCore(system: String): Boolean
    external fun loadRom(path: String): Boolean
    external fun runFrame(): Int
    external fun reset()

    fun safeInit(system: String): Boolean = if (available) initCore(system) else false
    fun safeLoadRom(path: String): Boolean = if (available) loadRom(path) else false
}
