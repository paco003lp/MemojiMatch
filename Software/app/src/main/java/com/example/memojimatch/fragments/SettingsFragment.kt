package com.example.memojimatch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.memojimatch.MemojiApp
import com.example.memojimatch.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settings = (requireActivity().application as MemojiApp).settings

        val switchSound = view.findViewById<SwitchMaterial>(R.id.switchSound)
        val switchVibration = view.findViewById<SwitchMaterial>(R.id.switchVibration)

        switchSound.isChecked = settings.isSoundEnabled
        switchVibration.isChecked = settings.isVibrationEnabled

        switchSound.setOnCheckedChangeListener { _, isChecked ->
            settings.isSoundEnabled = isChecked
        }
        switchVibration.setOnCheckedChangeListener { _, isChecked ->
            settings.isVibrationEnabled = isChecked
        }
    }
}