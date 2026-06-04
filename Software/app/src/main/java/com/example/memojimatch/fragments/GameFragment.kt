package com.example.memojimatch.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.memojimatch.R
import kotlin.math.max
import kotlin.math.min

class GameFragment : Fragment() {

    enum class Level { EASY, MEDIUM, HARD }

    private data class Card(val emoji: String, var matched: Boolean = false, var faceUp: Boolean = false)

    private val EMOJIS = listOf(
        "🐶","🐱","🦊","🐼","🦁","🐸","🐵","🦄","🐷","🐔","🐧","🐢",
        "🦋","🐝","🍎","🍕","🍔","🎈","⚽","🚀","🎸","🎮","🍩","🌈"
    )

    private val PENALTY = 10   // koliko se gubi po promašaju
    private val MATCH_POINTS = 100

    private lateinit var level: Level
    private var cols = 4
    private var rows = 4
    private var timeMs = 60_000L
    private var totalPairs = 8

    private lateinit var cards: List<Card>
    private lateinit var cardViews: List<TextView>

    private var firstIndex: Int? = null
    private var isBusy = false
    private var timerStarted = false
    private var hasMatchedAny = false
    private var score = 0
    private var matchedPairs = 0

    private var timer: CountDownTimer? = null
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var txtTime: TextView
    private lateinit var txtScore: TextView
    private lateinit var txtMode: TextView
    private lateinit var txtPairs: TextView
    private lateinit var txtPenalty: TextView
    private lateinit var progressTime: ProgressBar
    private lateinit var gridContainer: FrameLayout
    private lateinit var gridGame: GridLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_game, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtTime = view.findViewById(R.id.txtTime)
        txtScore = view.findViewById(R.id.txtScore)
        txtMode = view.findViewById(R.id.txtMode)
        txtPairs = view.findViewById(R.id.txtPairs)
        txtPenalty = view.findViewById(R.id.txtPenalty)
        progressTime = view.findViewById(R.id.progressTime)
        gridContainer = view.findViewById(R.id.gridContainer)
        gridGame = view.findViewById(R.id.gridGame)

        level = try {
            Level.valueOf(arguments?.getString("level") ?: "EASY")
        } catch (e: Exception) { Level.EASY }

        when (level) {
            Level.EASY -> { cols = 4; rows = 4; timeMs = 60_000 }
            Level.MEDIUM -> { cols = 4; rows = 6; timeMs = 90_000 }
            Level.HARD -> { cols = 6; rows = 6; timeMs = 120_000 }
        }
        totalPairs = (rows * cols) / 2

        txtMode.text = level.name
        txtTime.text = formatTime((timeMs / 1000).toInt())
        progressTime.progress = 100
        updateHud()

        // izmiješaj kartice
        val chosen = EMOJIS.shuffled().take(totalPairs)
        val deck = (chosen + chosen).shuffled()
        cards = deck.map { Card(it) }

        // izgradi grid kad znamo dimenzije containera
        gridContainer.post { buildGrid() }
    }

    private fun buildGrid() {
        val m = dp(4)
        val cellByW = gridContainer.width / cols - 2 * m
        val cellByH = gridContainer.height / rows - 2 * m
        val cell = min(cellByW, cellByH).coerceAtLeast(dp(40))

        gridGame.removeAllViews()
        gridGame.columnCount = cols

        cardViews = cards.mapIndexed { index, _ ->
            val tv = TextView(requireContext())
            val lp = GridLayout.LayoutParams()
            lp.width = cell
            lp.height = cell
            lp.setMargins(m, m, m, m)
            tv.layoutParams = lp
            tv.gravity = Gravity.CENTER
            tv.setBackgroundResource(R.drawable.bg_card_back)
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, cell * 0.45f)
            tv.setOnClickListener { onCardClicked(index) }
            gridGame.addView(tv)
            tv
        }
    }

    private fun onCardClicked(index: Int) {
        if (isBusy) return
        val card = cards[index]
        if (card.matched || card.faceUp) return

        if (!timerStarted) startTimer()

        flipUp(index)

        val first = firstIndex
        if (first == null) {
            firstIndex = index
        } else {
            isBusy = true
            if (cards[first].emoji == card.emoji) {
                cards[first].matched = true
                cards[index].matched = true
                cardViews[first].setBackgroundResource(R.drawable.bg_card_matched)
                cardViews[index].setBackgroundResource(R.drawable.bg_card_matched)
                matchedPairs++
                score += MATCH_POINTS
                hasMatchedAny = true
                updateHud()
                firstIndex = null
                isBusy = false
                if (matchedPairs == totalPairs) onWin()
            } else {
                if (hasMatchedAny) {
                    score = max(0, score - PENALTY)
                    showPenalty()
                    updateHud()
                }
                handler.postDelayed({
                    flipDown(first)
                    flipDown(index)
                    firstIndex = null
                    isBusy = false
                }, 700)
            }
        }
    }

    private fun flipUp(index: Int) {
        cards[index].faceUp = true
        cardViews[index].text = cards[index].emoji
        cardViews[index].setBackgroundResource(R.drawable.bg_card_front)
    }

    private fun flipDown(index: Int) {
        if (cards[index].matched) return
        cards[index].faceUp = false
        cardViews[index].text = ""
        cardViews[index].setBackgroundResource(R.drawable.bg_card_back)
    }

    private fun startTimer() {
        timerStarted = true
        timer = object : CountDownTimer(timeMs, 100) {
            override fun onTick(ms: Long) {
                txtTime.text = formatTime((ms / 1000).toInt())
                progressTime.progress = ((ms.toFloat() / timeMs) * 100).toInt()
            }
            override fun onFinish() {
                txtTime.text = "0:00"
                progressTime.progress = 0
                onTimeUp()
            }
        }.start()
    }

    private fun updateHud() {
        txtScore.text = score.toString()
        txtPairs.text = "$matchedPairs/$totalPairs parova"
    }

    private fun showPenalty() {
        txtPenalty.text = "-$PENALTY kazna"
        txtPenalty.visibility = View.VISIBLE
        handler.postDelayed({ txtPenalty.visibility = View.INVISIBLE }, 800)
    }

    private fun onWin() {
        timer?.cancel()
        saveScore()
        endDialog("Bravo! 🎉", "Spojio si sve parove!\nBodovi: $score")
    }

    private fun onTimeUp() {
        isBusy = true
        saveScore()
        endDialog("Vrijeme je isteklo ⏱", "Parovi: $matchedPairs/$totalPairs\nBodovi: $score")
    }

    private fun endDialog(title: String, message: String) {
        if (!isAdded) return
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Natrag") { _, _ -> findNavController().popBackStack() }
            .show()
    }

    private fun saveScore() {
        val prefs = requireContext()
            .getSharedPreferences("user_profile", android.content.Context.MODE_PRIVATE)
        val levelKey = when (level) {
            Level.EASY -> "best_easy"
            Level.MEDIUM -> "best_medium"
            Level.HARD -> "best_hard"
        }
        val prevLevel = prefs.getInt(levelKey, 0)
        val prevBest = prefs.getInt("best_score", 0)
        prefs.edit()
            .putInt(levelKey, max(prevLevel, score))
            .putInt("best_score", max(prevBest, score))
            .apply()
    }

    private fun formatTime(totalSeconds: Int): String {
        val m = totalSeconds / 60
        val s = totalSeconds % 60
        return "%d:%02d".format(m, s)
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        handler.removeCallbacksAndMessages(null)
    }
}