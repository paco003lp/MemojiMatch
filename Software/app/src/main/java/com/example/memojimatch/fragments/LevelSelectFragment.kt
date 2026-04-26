package com.example.memojimatch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.memojimatch.R

class LevelSelectFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_level_select, container, false)

        val btnPlay = view.findViewById<Button>(R.id.btnPlay)
        btnPlay.setOnClickListener {
            findNavController().navigate(R.id.gameFragment)
        }

        return view
    }
}
