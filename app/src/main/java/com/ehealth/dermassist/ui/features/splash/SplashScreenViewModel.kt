package com.ehealth.dermassist.ui.features.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehealth.dermassist.domain.usecase.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SplashScreenViewModel
@Inject
constructor(private val signInWithGoogleUseCase: SignInWithGoogleUseCase) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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
