package com.example.memojimatch.ui.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.memojimatch.R

class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_main, container, false)
}