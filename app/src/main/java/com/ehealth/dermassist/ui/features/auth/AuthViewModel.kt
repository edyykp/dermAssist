package com.ehealth.dermassist.ui.features.auth

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehealth.dermassist.domain.model.User
import com.ehealth.dermassist.domain.repository.AppRepository
import com.ehealth.dermassist.domain.usecase.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel
@Inject
constructor(
    private val repository: AppRepository,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    val user: StateFlow<User?> =
        repository.getUser().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isLoggedIn: StateFlow<Boolean?> =
        repository
            .getUser()
            .map { it != null }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun handleGoogleSignIn(context: Context, onSuccess: () -> Unit) {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            val result = signInWithGoogleUseCase(context)
            if (result.isSuccess) {
                onSuccess()
            } else {
                _authError.value = result.exceptionOrNull()?.message ?: "Google Sign-In failed"
            }
            _isLoading.value = false
        }
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            val result = repository.loginWithEmail(email, pass)
            if (result.isSuccess) {
                onSuccess()
            } else {
                _authError.value = result.exceptionOrNull()?.message ?: "Login failed"
            }
            _isLoading.value = false
        }
    }

    fun signUp(email: String, pass: String, name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            val result = repository.signUpWithEmail(email, pass, name)
            if (result.isSuccess) {
                onSuccess()
            } else {
                _authError.value = result.exceptionOrNull()?.message ?: "Sign up failed"
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _authError.value = null
    }

    fun isEmailValid(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    fun isFullNameValid(fullName: String): Boolean {
        return fullName.trim().isNotEmpty()
    }

    fun doPasswordsMatch(password: String, confirm: String): Boolean {
        return password == confirm
    }
}
