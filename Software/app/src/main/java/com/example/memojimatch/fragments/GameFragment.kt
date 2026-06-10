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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.memojimatch.R
import com.example.memojimatch.data.Level
import com.example.memojimatch.viewmodels.CardState
import com.example.memojimatch.viewmodels.GameViewModel
import kotlin.math.max
import kotlin.math.min

class GameFragment : Fragment() {

    private val viewModel: GameViewModel by viewModels()

    private val PENALTY = 10
    private val MATCH_POINTS = 100

    private val EMOJIS = listOf(
        "🐶","🐱","🦊","🐼","🦁","🐸","🐵","🦄","🐷","🐔","🐧","🐢",
        "🦋","🐝","🍎","🍕","🍔","🎈","⚽","🚀","🎸","🎮","🍩","🌈"
    )

    private lateinit var level: Level
    private var cols = 4
    private var rows = 4
    private var timeMs = 60_000L
    private var totalPairs = 8

    private lateinit var cardViews: List<TextView>
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

        txtTime      = view.findViewById(R.id.txtTime)
        txtScore     = view.findViewById(R.id.txtScore)
        txtMode      = view.findViewById(R.id.txtMode)
        txtPairs     = view.findViewById(R.id.txtPairs)
        txtPenalty   = view.findViewById(R.id.txtPenalty)
        progressTime = view.findViewById(R.id.progressTime)
        gridContainer = view.findViewById(R.id.gridContainer)
        gridGame     = view.findViewById(R.id.gridGame)

        level = try {
            Level.valueOf(arguments?.getString("level") ?: "EASY")
        } catch (e: Exception) { Level.EASY }

        when (level) {
            Level.EASY   -> { cols = 4; rows = 4; timeMs = 60_000 }
            Level.MEDIUM -> { cols = 4; rows = 6; timeMs = 90_000 }
            Level.HARD   -> { cols = 6; rows = 6; timeMs = 120_000 }
        }
        totalPairs = (rows * cols) / 2

        txtMode.text = level.name

        // Inicijaliziraj igru samo ako nije već pokrenuta (preživljava rotaciju)
        if (!viewModel.isInitialized) {
            val chosen = EMOJIS.shuffled().take(totalPairs)
            val deck = (chosen + chosen).shuffled()
            viewModel.initGame(deck.map { CardState(it) }, timeMs)
        }

        gridContainer.post { buildGrid() }
    }

    private fun buildGrid() {
        val state = viewModel.gameState.value
        val m = dp(4)
        val cellByW = gridContainer.width / cols - 2 * m
        val cellByH = gridContainer.height / rows - 2 * m
        val cell = min(cellByW, cellByH).coerceAtLeast(dp(40))

        gridGame.removeAllViews()
        gridGame.columnCount = cols

        cardViews = state.cards.mapIndexed { index, card ->
            val tv = TextView(requireContext())
            val lp = GridLayout.LayoutParams()
            lp.width = cell; lp.height = cell; lp.setMargins(m, m, m, m)
            tv.layoutParams = lp
            tv.gravity = Gravity.CENTER
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, cell * 0.45f)
            tv.setOnClickListener { onCardClicked(index) }
            // Vrati vizualni prikaz prema sačuvanom stanju
            when {
                card.matched  -> { tv.text = card.emoji; tv.setBackgroundResource(R.drawable.bg_card_matched) }
                card.faceUp   -> { tv.text = card.emoji; tv.setBackgroundResource(R.drawable.bg_card_front) }
                else          -> { tv.text = ""; tv.setBackgroundResource(R.drawable.bg_card_back) }
            }
            gridGame.addView(tv)
            tv
        }

        // Obnovi HUD
        val s = viewModel.gameState.value
        txtScore.text = s.score.toString()
        txtPairs.text = getString(R.string.game_pairs, s.matchedPairs, totalPairs)
        txtTime.text  = formatTime((s.timeRemainingMs / 1000).toInt())
        progressTime.progress = ((s.timeRemainingMs.toFloat() / timeMs) * 100).toInt()

        // Nastavi timer ako je bio pokrenut
        if (s.timerStarted && !s.finished) startTimer(s.timeRemainingMs)
    }

    private fun onCardClicked(index: Int) {
        val s = viewModel.gameState.value
        if (s.isBusy || s.finished) return
        val card = s.cards[index]
        if (card.matched || card.faceUp) return

        if (!s.timerStarted) startTimer(s.timeRemainingMs)

        flipUp(index)

        val first = s.firstIndex
        if (first == null) {
            viewModel.updateState { copy(firstIndex = index) }
        } else {
            viewModel.updateState { copy(isBusy = true) }
            val updatedCards = viewModel.gameState.value.cards.toMutableList()
            if (updatedCards[first].emoji == updatedCards[index].emoji) {
                updatedCards[first] = updatedCards[first].copy(matched = true)
                updatedCards[index] = updatedCards[index].copy(matched = true)
                cardViews[first].setBackgroundResource(R.drawable.bg_card_matched)
                cardViews[index].setBackgroundResource(R.drawable.bg_card_matched)
                val newMatched = s.matchedPairs + 1
                val newScore   = s.score + MATCH_POINTS
                viewModel.updateState {
                    copy(
                        cards = updatedCards,
                        matchedPairs = newMatched,
                        score = newScore,
                        hasMatchedAny = true,
                        firstIndex = null,
                        isBusy = false
                    )
                }
                updateHud()
                if (newMatched == totalPairs) onWin()
            } else {
                val newErrors = s.errorCount + 1
                val newScore  = if (s.hasMatchedAny) max(0, s.score - PENALTY) else s.score
                viewModel.updateState {
                    copy(errorCount = newErrors, score = newScore, firstIndex = null)
                }
                if (s.hasMatchedAny) { showPenalty(); updateHud() }
                handler.postDelayed({
                    flipDown(first)
                    flipDown(index)
                    viewModel.updateState { copy(isBusy = false) }
                }, 700)
            }
        }
    }

    private fun flipUp(index: Int) {
        val cards = viewModel.gameState.value.cards.toMutableList()
        cards[index] = cards[index].copy(faceUp = true)
        viewModel.updateState { copy(cards = cards) }
        cardViews[index].text = cards[index].emoji
        cardViews[index].setBackgroundResource(R.drawable.bg_card_front)
    }

    private fun flipDown(index: Int) {
        val cards = viewModel.gameState.value.cards.toMutableList()
        if (cards[index].matched) return
        cards[index] = cards[index].copy(faceUp = false)
        viewModel.updateState { copy(cards = cards) }
        cardViews[index].text = ""
        cardViews[index].setBackgroundResource(R.drawable.bg_card_back)
    }

    private fun startTimer(fromMs: Long) {
        timer?.cancel()
        viewModel.updateState { copy(timerStarted = true) }
        timer = object : CountDownTimer(fromMs, 100) {
            override fun onTick(ms: Long) {
                viewModel.updateState { copy(timeRemainingMs = ms) }
                txtTime.text = formatTime((ms / 1000).toInt())
                progressTime.progress = ((ms.toFloat() / timeMs) * 100).toInt()
            }
            override fun onFinish() {
                viewModel.updateState { copy(timeRemainingMs = 0) }
                txtTime.text = getString(R.string.game_time_zero)
                progressTime.progress = 0
                onTimeUp()
            }
        }.start()
    }

    private fun updateHud() {
        val s = viewModel.gameState.value
        txtScore.text = s.score.toString()
        txtPairs.text = getString(R.string.game_pairs, s.matchedPairs, totalPairs)
    }

    private fun showPenalty() {
        txtPenalty.text = getString(R.string.game_penalty, PENALTY)
        txtPenalty.visibility = View.VISIBLE
        handler.postDelayed({ txtPenalty.visibility = View.INVISIBLE }, 800)
    }

    private fun onWin() {
        timer?.cancel()
        val s = viewModel.gameState.value
        viewModel.updateState { copy(finished = true) }
        viewModel.saveResult(s.score, level.name, (s.timeRemainingMs / 1000).toInt(), true)
        navigateToResult(won = true)
    }

    private fun onTimeUp() {
        val s = viewModel.gameState.value
        viewModel.updateState { copy(finished = true, isBusy = true) }
        viewModel.saveResult(s.score, level.name, 0, false)
        navigateToResult(won = false)
    }

    private fun navigateToResult(won: Boolean) {
        if (!isAdded) return
        val s = viewModel.gameState.value
        val bundle = Bundle().apply {
            putInt("score", s.score)
            putInt("timeRemainingSeconds", (s.timeRemainingMs / 1000).toInt())
            putInt("errorCount", s.errorCount)
            putInt("matchedPairs", s.matchedPairs)
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
