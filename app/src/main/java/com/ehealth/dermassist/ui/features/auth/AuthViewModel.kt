package com.ehealth.dermassist.ui.features.auth

import android.content.Context
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
            val result = signInWithGoogleUseCase(context)
            if (result.isSuccess) {
                onSuccess()
            }
            _isLoading.value = false
        }
    }
}
