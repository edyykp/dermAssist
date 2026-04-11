package com.ehealth.dermassist.ui.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehealth.dermassist.domain.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(private val repository: AppRepository) :
    ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun logout() {
        viewModelScope.launch { repository.logout() }
    }

    fun clearUserData(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.clearUserData()
            if (result.isSuccess) {
                onSuccess()
            }
            _isLoading.value = false
        }
    }
}
