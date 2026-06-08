package com.example.memojimatch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.memojimatch.R
import com.example.memojimatch.adapters.LeaderboardAdapter
import com.example.memojimatch.data.ProfileRepository
import com.example.memojimatch.viewmodels.LeaderboardViewModel
import kotlinx.coroutines.launch

class LeaderboardFragment : Fragment() {

    private val viewModel: LeaderboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_leaderboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUsername = ProfileRepository(requireContext()).getUsername()
        val adapter = LeaderboardAdapter(currentUsername)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)

        view.findViewById<RecyclerView>(R.id.rvLeaderboard).adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.topResults.collect { results ->
                    adapter.submitList(results)
                    tvEmpty.visibility = if (results.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }

        view.findViewById<Button>(R.id.btnGoToHistory).setOnClickListener {
            findNavController().navigate(R.id.action_leaderboard_to_history)
        }
    }
}
