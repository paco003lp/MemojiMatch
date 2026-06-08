package com.example.memojimatch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.memojimatch.R
import com.example.memojimatch.data.ProfileRepository

class ResultFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_result, container, false)

        val args = arguments
        val score         = args?.getInt("score", 0) ?: 0
        val timeRemaining = args?.getInt("timeRemainingSeconds", 0) ?: 0
        val errorCount    = args?.getInt("errorCount", 0) ?: 0
        val matchedPairs  = args?.getInt("matchedPairs", 0) ?: 0
        val totalPairs    = args?.getInt("totalPairs", 0) ?: 0
        val levelName     = args?.getString("levelName", "EASY") ?: "EASY"
        val won           = args?.getBoolean("won", false) ?: false

        val username = ProfileRepository(requireContext()).getUsername()

        val levelDisplay = levelName.lowercase().replaceFirstChar { it.uppercase() }

        view.findViewById<TextView>(R.id.tvWinMsg).text =
            if (won) getString(R.string.msg_win) else getString(R.string.msg_time_up)
        view.findViewById<android.widget.ImageView>(R.id.ivWinIcon).apply {
            // koristimo unicode emoji kao tag da zaobiđemo nedostatak asset-a
            contentDescription = if (won) "🎉" else "⏱"
        }

        view.findViewById<TextView>(R.id.tvFinalScore).text   = score.toString()
        view.findViewById<TextView>(R.id.tvLevelUser).text    = "$levelDisplay - $username"
        view.findViewById<TextView>(R.id.tvTimeRemaining).text = formatTime(timeRemaining)
        view.findViewById<TextView>(R.id.tvErrorCount).text   = "-$errorCount"
        view.findViewById<TextView>(R.id.tvPairsCount).text   = "$matchedPairs/$totalPairs"

        view.findViewById<Button>(R.id.btnNewGame).setOnClickListener {
            val bundle = Bundle().apply { putString("level", levelName) }
            findNavController().navigate(R.id.gameFragment, bundle)
        }
        view.findViewById<Button>(R.id.btnMenu).setOnClickListener {
            findNavController().navigate(R.id.levelSelectFragment)
        }

        return view
    }

    private fun formatTime(totalSeconds: Int): String {
        val m = totalSeconds / 60
        val s = totalSeconds % 60
        return "%d:%02d".format(m, s)
    }
}
