package com.example.memojimatch.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.memojimatch.R
import com.example.memojimatch.data.ProfileRepository
import androidx.navigation.fragment.findNavController

class LevelSelectFragment : Fragment() {

    enum class Level { EASY, MEDIUM, HARD }
    private var selectedLevel = Level.EASY

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

        txtAvatar = view.findViewById(R.id.txtAvatar)
        txtName = view.findViewById(R.id.txtName)
        txtRecord = view.findViewById(R.id.txtRecord)
        cardEasy = view.findViewById(R.id.cardEasy)
        cardMedium = view.findViewById(R.id.cardMedium)
        cardHard = view.findViewById(R.id.cardHard)
        badgeEasy = view.findViewById(R.id.badgeEasy)
        badgeMedium = view.findViewById(R.id.badgeMedium)
        badgeHard = view.findViewById(R.id.badgeHard)
        btnPlay = view.findViewById(R.id.btnPlay)

        loadProfile()
        loadScores()

        cardEasy.setOnClickListener { selectLevel(Level.EASY) }
        cardMedium.setOnClickListener { selectLevel(Level.MEDIUM) }
        cardHard.setOnClickListener { selectLevel(Level.HARD) }
        selectLevel(selectedLevel)

        btnPlay.setOnClickListener {
            val bundle = Bundle().apply { putString("level", selectedLevel.name) }
            findNavController().navigate(R.id.action_main_to_game, bundle)
        }
    }

    private fun selectLevel(level: Level) {
        selectedLevel = level
        cardEasy.isSelected = level == Level.EASY
        cardMedium.isSelected = level == Level.MEDIUM
        cardHard.isSelected = level == Level.HARD
        badgeEasy.isSelected = level == Level.EASY
        badgeMedium.isSelected = level == Level.MEDIUM
        badgeHard.isSelected = level == Level.HARD
    }

    private fun loadProfile() {
        val repo = ProfileRepository(requireContext())
        txtName.text = repo.getUsername().ifBlank { "Igrač" }
        txtAvatar.text = repo.getAvatar() ?: "🐼"
    }

    private fun loadScores() {
        val prefs = requireContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE)
        txtRecord.text = "REKORD:" + prefs.getInt("best_score", 0)
        badgeEasy.text = prefs.getInt("best_easy", 0).toString()
        badgeMedium.text = prefs.getInt("best_medium", 0).toString()
        badgeHard.text = prefs.getInt("best_hard", 0).toString()
    }
}