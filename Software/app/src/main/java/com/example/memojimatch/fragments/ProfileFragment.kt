package com.example.memojimatch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.memojimatch.R

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val btnSave = view.findViewById<Button>(R.id.btnSaveProfile)
        btnSave.setOnClickListener {
            findNavController().navigate(R.id.levelSelectFragment)
        }

        return view
    }
}