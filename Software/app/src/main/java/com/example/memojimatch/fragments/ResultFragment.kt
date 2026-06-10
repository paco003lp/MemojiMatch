package com.example.memojimatch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.memojimatch.R
import com.example.memojimatch.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val score         = args?.getInt("score", 0) ?: 0
        val timeRemaining = args?.getInt("timeRemainingSeconds", 0) ?: 0
        val errorCount    = args?.getInt("errorCount", 0) ?: 0
        val matchedPairs  = args?.getInt("matchedPairs", 0) ?: 0
        val totalPairs    = args?.getInt("totalPairs", 0) ?: 0
        val levelName     = args?.getString("levelName", "EASY") ?: "EASY"
        val won           = args?.getBoolean("won", false) ?: false
        val username      = args?.getString("username", "") ?: ""
        val levelDisplay  = levelName.lowercase().replaceFirstChar { it.uppercase() }

        binding.tvWinMsg.text       = if (won) getString(R.string.msg_win) else getString(R.string.msg_time_up)
        binding.tvFinalScore.text   = score.toString()
        binding.tvLevelUser.text    = getString(R.string.result_level_user, levelDisplay, username)
        binding.tvTimeRemaining.text = formatTime(timeRemaining)
        binding.tvErrorCount.text   = getString(R.string.result_error_count, errorCount)
        binding.tvPairsCount.text   = getString(R.string.result_pairs_count, matchedPairs, totalPairs)

        binding.btnNewGame.setOnClickListener {
            val bundle = Bundle().apply { putString("level", levelName) }
            findNavController().navigate(R.id.gameFragment, bundle)
        }
        binding.btnMenu.setOnClickListener {
            findNavController().navigate(R.id.levelSelectFragment)
        }
    }

    private fun formatTime(totalSeconds: Int): String {
        val m = totalSeconds / 60
        val s = totalSeconds % 60
        return "%d:%02d".format(m, s)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
