package com.example.memojimatch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.memojimatch.R

class ResultFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_result, container, false)

        val btnNewGame = view.findViewById<Button>(R.id.btnNewGame)
        btnNewGame.setOnClickListener {
            findNavController().navigate(R.id.levelSelectFragment)
        }

        val btnMenu = view.findViewById<Button>(R.id.btnMenu)
        btnMenu.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }

        return view
    }
}
