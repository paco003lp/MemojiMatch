package com.example.memojimatch

import android.app.Application
import com.example.memojimatch.data.SettingsManager

class MemojiApp : Application() {
    val settings by lazy { SettingsManager(this) }
}