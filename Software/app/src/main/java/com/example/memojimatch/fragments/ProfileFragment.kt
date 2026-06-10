package com.example.memojimatch.fragments

import android.os.Bundle
import android.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memojimatch.R
import com.example.memojimatch.adapters.AvatarAdapter
import com.example.memojimatch.data.AvatarList
import com.example.memojimatch.data.ProfileRepository
import com.example.memojimatch.databinding.FragmentProfileBinding
import com.example.memojimatch.viewmodels.ProfileViewModel
import com.example.memojimatch.viewmodels.ProfileViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(ProfileRepository(requireContext()))
    }

    private lateinit var adapter: AvatarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = AvatarAdapter(AvatarList.avatars) { emoji -> viewModel.selectAvatar(emoji) }
        binding.avatarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 5)
        binding.avatarRecyclerView.adapter = adapter

        binding.usernameInput.doAfterTextChanged {
            viewModel.setUsername(it?.toString() ?: "")
        }

        binding.continueButton.setOnClickListener {
            viewModel.saveProfile()
        }

        observeViewModel()
    }

    private fun navigateToGame() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav)
            .selectedItemId = R.id.levelSelectFragment
    }

    private fun observeViewModel() {
        viewModel.username.observe(viewLifecycleOwner) { name ->
            // postavi samo na učitavanju (izbjegni petlju s text watcherom)
            if (binding.usernameInput.text.toString() != name) {
                binding.usernameInput.setText(name)
            }
        }
        viewModel.selectedAvatar.observe(viewLifecycleOwner) { emoji ->
            adapter.setSelected(emoji)
            binding.selectedAvatarPreview.text = emoji ?: "🙂"
        }
        viewModel.canConfirm.observe(viewLifecycleOwner) { enabled ->
            binding.continueButton.isEnabled = enabled
            binding.continueButton.alpha = if (enabled) 1f else 0.4f
        }
        viewModel.profileSaved.observe(viewLifecycleOwner) { saved ->
            if (saved) {
                viewModel.onNavigated()
                navigateToGame()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}