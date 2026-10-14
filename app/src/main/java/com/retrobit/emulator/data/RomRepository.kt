package com.retrobit.emulator.data

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject

class RomRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("retrobit", Context.MODE_PRIVATE)
    private val key = "roms"

    fun load(): MutableList<Rom> {
        val raw = prefs.getString(key, "[]") ?: "[]"
        val arr = JSONArray(raw)
        val list = mutableListOf<Rom>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(
                Rom(
                    id = o.getString("id"),
                    name = o.getString("name"),
                    uri = o.getString("uri"),
                    system = o.getString("system"),
                    sizeBytes = o.optLong("size", 0),
                    addedAt = o.optLong("added", System.currentTimeMillis()),
                )
            )
        }
        return list
    }

    fun save(list: List<Rom>) {
        val arr = JSONArray()
        list.forEach { r ->
            arr.put(JSONObject().apply {
                put("id", r.id)
                put("name", r.name)
                put("uri", r.uri)
                put("system", r.system)
                put("size", r.sizeBytes)
                put("added", r.addedAt)
            })
        }
        prefs.edit().putString(key, arr.toString()).apply()
    }

    fun add(rom: Rom) {
        val list = load()
        list.add(rom)
        save(list)
    }

    fun remove(id: String) {
        save(load().filterNot { it.id == id })
    }
}
