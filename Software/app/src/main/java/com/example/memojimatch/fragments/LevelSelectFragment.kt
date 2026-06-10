package com.example.memojimatch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.memojimatch.R
import com.example.memojimatch.data.Level
import com.example.memojimatch.viewmodels.LevelSelectViewModel
import kotlinx.coroutines.launch

class LevelSelectFragment : Fragment() {

    private var selectedLevel = Level.EASY

    private val viewModel: LevelSelectViewModel by viewModels()

    private lateinit var txtAvatar: TextView
    private lateinit var txtName: TextView
    private lateinit var txtRecord: TextView
    private lateinit var cardEasy: LinearLayout
    private lateinit var cardMedium: LinearLayout
    private lateinit var cardHard: LinearLayout
    private lateinit var badgeEasy: TextView
    private lateinit var badgeMedium: TextView
    private lateinit var badgeHard: TextView
    private lateinit var btnPlay: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_level_select, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtAvatar  = view.findViewById(R.id.txtAvatar)
        txtName    = view.findViewById(R.id.txtName)
        txtRecord  = view.findViewById(R.id.txtRecord)
        cardEasy   = view.findViewById(R.id.cardEasy)
        cardMedium = view.findViewById(R.id.cardMedium)
        cardHard   = view.findViewById(R.id.cardHard)
        badgeEasy  = view.findViewById(R.id.badgeEasy)
        badgeMedium = view.findViewById(R.id.badgeMedium)
        badgeHard  = view.findViewById(R.id.badgeHard)
        btnPlay    = view.findViewById(R.id.btnPlay)

        cardEasy.setOnClickListener { selectLevel(Level.EASY) }
        cardMedium.setOnClickListener { selectLevel(Level.MEDIUM) }
        cardHard.setOnClickListener { selectLevel(Level.HARD) }
        selectLevel(selectedLevel)

        btnPlay.setOnClickListener {
            val bundle = Bundle().apply { putString("level", selectedLevel.name) }
            findNavController().navigate(R.id.action_main_to_game, bundle)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    txtName.text   = state.username
                    txtAvatar.text = state.avatarEmoji
                    txtRecord.text = getString(R.string.label_record, state.bestScore)
                    badgeEasy.text   = state.bestEasy.toString()
                    badgeMedium.text = state.bestMedium.toString()
                    badgeHard.text   = state.bestHard.toString()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch { viewModel.load() }
    }

    private fun selectLevel(level: Level) {
        selectedLevel = level
        cardEasy.isSelected   = level == Level.EASY
        cardMedium.isSelected = level == Level.MEDIUM
        cardHard.isSelected   = level == Level.HARD
        badgeEasy.isSelected   = level == Level.EASY
        badgeMedium.isSelected = level == Level.MEDIUM
        badgeHard.isSelected   = level == Level.HARD
    }
}
