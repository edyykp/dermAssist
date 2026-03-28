package com.ehealth.dermassist.ui.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehealth.dermassist.domain.model.User
import com.ehealth.dermassist.domain.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {

    val user: StateFlow<User?> =
        repository.getUser().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isLoggedIn: StateFlow<Boolean?> =
        repository
            .getUser()
            .map { it != null }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun signInWithGoogle(idToken: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = repository.signInWithGoogle(idToken)
            onResult(result)
        }
    }

    fun logout() {
        viewModelScope.launch { repository.logout() }
    }
}
