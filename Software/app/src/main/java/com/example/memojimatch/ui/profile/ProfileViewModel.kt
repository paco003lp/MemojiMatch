package com.example.memojimatch.ui.profile

import androidx.lifecycle.*
import com.example.memojimatch.data.ProfileRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _username = MutableLiveData("")
    val username: LiveData<String> = _username

    private val _selectedAvatar = MutableLiveData<String?>(null)
    val selectedAvatar: LiveData<String?> = _selectedAvatar

    val canConfirm = MediatorLiveData<Boolean>().apply {
        value = false
        addSource(_username) { value = validate() }
        addSource(_selectedAvatar) { value = validate() }
    }

    init {
        viewModelScope.launch {
            _username.value = repository.getUsername()
            _selectedAvatar.value = repository.getAvatarEmoji()
        }
    }

    private fun validate(): Boolean =
        !_username.value.isNullOrBlank() && _selectedAvatar.value != null

    fun setUsername(value: String) { _username.value = value }
    fun selectAvatar(emoji: String) { _selectedAvatar.value = emoji }

    fun saveProfile() {
        val name = _username.value?.trim() ?: return
        val emoji = _selectedAvatar.value ?: return
        viewModelScope.launch {
            repository.saveProfile(name, emoji)
        }
    }
}

class ProfileViewModelFactory(
    private val repository: ProfileRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ProfileViewModel(repository) as T
}
