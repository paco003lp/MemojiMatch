package com.example.memojimatch.data

import android.content.Context

class ProfileRepository(private val context: Context) {

    private val userDao = AppDatabase.getDatabase(context).userDao()

    suspend fun saveProfile(username: String, avatarEmoji: String) {
        userDao.insertUser(
            User(
                id = 1,
                username = username,
                avatarIndex = AvatarList.indexOf(avatarEmoji)
            )
        )
    }

    suspend fun getUsername(): String {
        val roomUser = userDao.getUser()
        if (!roomUser?.username.isNullOrBlank()) return roomUser!!.username

        // Jednokratna migracija iz SharedPreferences u Room
        return migrateFromSharedPreferences()
    }

    suspend fun getAvatarEmoji(): String {
        val roomUser = userDao.getUser()
        if (roomUser != null) return AvatarList.emojiAt(roomUser.avatarIndex)

        // Jednokratna migracija iz SharedPreferences u Room
        migrateFromSharedPreferences()
        return AvatarList.emojiAt(userDao.getUser()?.avatarIndex ?: 0)
    }

    private suspend fun migrateFromSharedPreferences(): String {
        val prefs = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "") ?: ""
        val avatarEmoji = prefs.getString("avatar", AvatarList.avatars.first()) ?: AvatarList.avatars.first()

        if (username.isNotBlank()) {
            saveProfile(username, avatarEmoji)
        }

        return username
    }
}
