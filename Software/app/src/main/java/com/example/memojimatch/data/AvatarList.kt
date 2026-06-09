package com.example.memojimatch.data

object AvatarList {
    val avatars = listOf("🐼", "🦊", "🐶", "🐱", "🦁", "🐸", "🐵", "🐰", "🐯", "🦄")

    fun emojiAt(index: Int): String = avatars.getOrElse(index) { avatars.first() }
    fun indexOf(emoji: String): Int = avatars.indexOf(emoji).takeIf { it >= 0 } ?: 0
}
