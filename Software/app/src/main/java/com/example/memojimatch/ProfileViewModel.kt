package com.example.memojimatch.ui.profile

import androidx.lifecycle.*
import com.example.memojimatch.data.ProfileRepository

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _username = MutableLiveData(repository.getUsername())
    val username: LiveData<String> = _username

    private val _selectedAvatar = MutableLiveData(repository.getAvatar())
    val selectedAvatar: LiveData<String?> = _selectedAvatar

    // Aktivan gumb samo kad postoje ime I avatar
    val canConfirm = MediatorLiveData<Boolean>().apply {
        value = validate()
        addSource(_username) { value = validate() }
        addSource(_selectedAvatar) { value = validate() }
    }

    private fun validate(): Boolean =
        !_username.value.isNullOrBlank() && _selectedAvatar.value != null

    fun setUsername(value: String) { _username.value = value }
    fun selectAvatar(emoji: String) { _selectedAvatar.value = emoji }

    fun saveProfile() {
        repository.saveProfile(_username.value!!.trim(), _selectedAvatar.value!!)
    }
}

class ProfileViewModelFactory(
    private val repository: ProfileRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ProfileViewModel(repository) as T
}