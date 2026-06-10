package com.example.memojimatch.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import com.example.memojimatch.data.SoundManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.memojimatch.MemojiApp
import com.example.memojimatch.R
import com.example.memojimatch.data.SettingsManager
import com.example.memojimatch.data.SoundType
import com.example.memojimatch.viewmodels.GameViewModel
import kotlin.math.max
import kotlin.math.min


class GameFragment : Fragment() {

    private lateinit var settings: SettingsManager

    private lateinit var soundManager: SoundManager


    private val viewModel: GameViewModel by viewModels()

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
    private var errorCount = 0
    private var timeRemainingMs = 0L

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
        settings = (requireActivity().application as MemojiApp).settings
        super.onViewCreated(view, savedInstanceState)

        soundManager = SoundManager(requireContext())

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
        timeRemainingMs = timeMs
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
        soundManager.play(SoundType.FLIP)

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

                animateMatch(cardViews[first])
                animateMatch(cardViews[index])
                soundManager.play(SoundType.MATCH)

                if (matchedPairs == totalPairs) {
                    handler.postDelayed({ onWin() }, 350)  // čeka da animacija završi
                }
            } else {
                errorCount++
                if (settings.isVibrationEnabled) {
                    val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(200)
                    }
                }
                soundManager.play(SoundType.WRONG)
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

    private fun animateMatch(cardView: View) {
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(cardView, "scaleX", 1f, 1.2f, 1f),
                ObjectAnimator.ofFloat(cardView, "scaleY", 1f, 1.2f, 1f)
            )
            duration = 300
            interpolator = OvershootInterpolator()
            start()
        }
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
                timeRemainingMs = ms
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
        viewModel.saveResult(
            score = score,
            difficulty = level.name,
            timeRemaining = (timeRemainingMs / 1000).toInt(),
            isWon = true
        )
        soundManager.play(SoundType.WIN)
        navigateToResult(won = true)
    }

    private fun onTimeUp() {
        isBusy = true
        viewModel.saveResult(
            score = score,
            difficulty = level.name,
            timeRemaining = (timeRemainingMs / 1000).toInt(),
            isWon = false
        )
        navigateToResult(won = false)
    }

    private fun navigateToResult(won: Boolean) {
        if (!isAdded) return
        val bundle = Bundle().apply {
            putInt("score", score)
            putInt("timeRemainingSeconds", (timeRemainingMs / 1000).toInt())
            putInt("errorCount", errorCount)
            putInt("matchedPairs", matchedPairs)
            putInt("totalPairs", totalPairs)
            putString("levelName", level.name)
            putBoolean("won", won)
            putString("username", viewModel.username.value)
        }
        findNavController().navigate(R.id.action_game_to_result, bundle)
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