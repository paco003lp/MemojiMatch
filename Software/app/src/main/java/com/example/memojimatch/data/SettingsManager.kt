package com.example.memojimatch.data

import android.content.Context
import androidx.core.content.edit

class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("memoji_prefs", Context.MODE_PRIVATE)
    var isSoundEnabled: Boolean
        get() = prefs.getBoolean("sound_enabled", true)
        set(value) = prefs.edit { putBoolean("sound_enabled", value) }
    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean("vibration_enabled", true)
        set(value) = prefs.edit { putBoolean("vibration_enabled", value) }
}