package com.example.memojimatch.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.memojimatch.R
import com.example.memojimatch.data.AppDatabase
import kotlinx.coroutines.launch
import com.example.memojimatch.data.User
class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val btnSave = view.findViewById<Button>(R.id.btnSaveProfile)
        btnSave.setOnClickListener {
            val username = view.findViewById<EditText>(R.id.etUsername).text.toString()

            val db = AppDatabase.getDatabase(requireContext())
            val userDao = db.userDao()

            lifecycleScope.launch {
                val newUser = User(username = username, avatarIndex = 1)
                userDao.insertUser(newUser)
                Log.d("TEST_BAZE", "Korisnik spremljen: $username")

                val savedUser = userDao.getUser()
                if (savedUser != null) {
                    Log.d("TEST_BAZE", "Pročitano iz baze: ${savedUser.username}, Index: ${savedUser.avatarIndex}")
                            findNavController().navigate(R.id.levelSelectFragment)
                }else{
                    Log.e("TEST_BAZE", "Greska: Korisnik nije pronaden u bazi!")
                }
            }
        }

        return view
    }
}