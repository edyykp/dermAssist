package com.ehealth.dermassist.ui.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehealth.dermassist.data.repository.AppRepositoryImpl
import com.ehealth.dermassist.domain.model.User
import com.ehealth.dermassist.domain.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AppRepository = AppRepositoryImpl()
) : ViewModel() {

    val user: StateFlow<User?> = repository.getUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun signInWithGoogle(idToken: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = repository.signInWithGoogle(idToken)
            onResult(result)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
