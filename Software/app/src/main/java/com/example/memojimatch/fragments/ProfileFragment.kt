package com.example.memojimatch.ui.profile

import android.os.Bundle
import android.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memojimatch.R
import com.example.memojimatch.data.ProfileRepository
import com.example.memojimatch.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(ProfileRepository(requireContext()))
    }

    private val avatars = listOf("🐼", "🦊", "🐶", "🐱", "🦁", "🐸", "🐵", "🐰", "🐯", "🦄")
    private lateinit var adapter: AvatarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = AvatarAdapter(avatars) { emoji -> viewModel.selectAvatar(emoji) }
        binding.avatarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 5)
        binding.avatarRecyclerView.adapter = adapter

        binding.usernameInput.doAfterTextChanged {
            viewModel.setUsername(it?.toString() ?: "")
        }

        binding.continueButton.setOnClickListener {
            viewModel.saveProfile()
            findNavController().navigate(R.id.action_profile_to_main)
        }

        observeViewModel()
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}