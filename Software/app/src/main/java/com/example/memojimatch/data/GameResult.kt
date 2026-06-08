package com.example.memojimatch.data

/**
 * Predstavlja jedan odigrani rezultat.
 * TODO: dodati @Entity i Room anotacije kada baza bude gotova.
 */
data class GameResult(
    val id: Long = 0,
    val username: String,
    val avatar: String,
    val score: Int,
    val level: String,
    val timestamp: Long,
    val matchedPairs: Int,
    val totalPairs: Int
)
