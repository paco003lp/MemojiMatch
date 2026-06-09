package com.example.memojimatch.data

import android.content.Context

class ProfileRepository(context: Context) {

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

    suspend fun getUsername(): String = userDao.getUser()?.username ?: ""

    suspend fun getAvatarEmoji(): String =
        AvatarList.emojiAt(userDao.getUser()?.avatarIndex ?: 0)
}
