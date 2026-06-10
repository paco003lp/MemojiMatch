package com.example.memojimatch.data

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.memojimatch.MemojiApp
import com.example.memojimatch.R

class SoundManager(context: Context) {

    private val settings = (context.applicationContext as MemojiApp).settings
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        ).build()

    fun play(sound: SoundType) {
        if (!settings.isSoundEnabled) return
        val id = when (sound) {
            SoundType.FLIP -> soundFlip
            SoundType.MATCH -> soundMatch
            SoundType.WRONG -> soundWrong
            SoundType.WIN -> soundWin
        }
        soundPool.play(id, 1f, 1f, 0, 0, 1f)
    }


    private val soundFlip = soundPool.load(context, R.raw.cardflip, 1)
    private val soundMatch = soundPool.load(context, R.raw.cardmatch, 1)
    private val soundWrong = soundPool.load(context, R.raw.incorrect, 1)
    private val soundWin = soundPool.load(context, R.raw.victory, 1)

    fun play(sound: SoundType, enabled: Boolean) {
        if (!enabled) return
        val id = when (sound) {
            SoundType.FLIP -> soundFlip
            SoundType.MATCH -> soundMatch
            SoundType.WRONG -> soundWrong
            SoundType.WIN -> soundWin
        }
        soundPool.play(id, 1f, 1f, 0, 0, 1f)
    }

    fun release() = soundPool.release()
}

enum class SoundType { FLIP, MATCH, WRONG, WIN }