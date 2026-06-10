package com.example.memojimatch.data

import android.content.Context

class ProfileRepository(private val context: Context) {

    private val userDao = AppDatabase.getDatabase(context).userDao()
    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun getCurrentUserId(): Long = prefs.getLong("current_user_id", -1L)

    fun isLoggedIn(): Boolean = getCurrentUserId() != -1L

    private fun setCurrentUserId(id: Long) {
        prefs.edit().putLong("current_user_id", id).apply()
    }

    suspend fun saveProfile(username: String, avatarEmoji: String): Long {
        val avatarIndex = AvatarList.indexOf(avatarEmoji)
        val existing = userDao.getUserByUsername(username)
        val userId: Long = if (existing != null) {
            userDao.insertUser(existing.copy(avatarIndex = avatarIndex))
            existing.id
        } else {
            userDao.insertUser(User(username = username, avatarIndex = avatarIndex))
        }
        setCurrentUserId(userId)
        return userId
    }

    suspend fun getCurrentUser(): User? {
        val id = getCurrentUserId()
        if (id == -1L) return null
        return userDao.getUserById(id)
    }

    suspend fun getUsername(): String = getCurrentUser()?.username ?: ""

    suspend fun getAvatarEmoji(): String {
        val user = getCurrentUser() ?: return AvatarList.avatars.first()
        return AvatarList.emojiAt(user.avatarIndex)
    }
}
