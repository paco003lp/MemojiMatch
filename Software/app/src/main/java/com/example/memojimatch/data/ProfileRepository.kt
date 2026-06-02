package com.example.memojimatch.data

import android.content.Context

class ProfileRepository(context: Context) {

    private val prefs =
        context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)

    fun saveProfile(username: String, avatar: String) {
        prefs.edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_AVATAR, avatar)
            .apply()
    }

    fun getUsername(): String = prefs.getString(KEY_USERNAME, "") ?: ""
    fun getAvatar(): String? = prefs.getString(KEY_AVATAR, null)

    companion object {
        private const val KEY_USERNAME = "username"
        private const val KEY_AVATAR = "avatar"
    }
}