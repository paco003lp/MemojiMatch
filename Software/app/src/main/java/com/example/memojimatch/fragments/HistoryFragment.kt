package com.example.memojimatch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.memojimatch.R
import com.example.memojimatch.adapters.HistoryAdapter
import com.example.memojimatch.viewmodels.HistoryViewModel
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_history, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HistoryAdapter()
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)

        view.findViewById<RecyclerView>(R.id.rvHistory).adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.lastFive.collect { results ->
                    adapter.submitList(results)
                    tvEmpty.visibility = if (results.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }
}
