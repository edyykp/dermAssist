package com.ehealth.dermassist.ui.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehealth.dermassist.domain.repository.AppRepository
import com.ehealth.dermassist.ui.LoadingStateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileScreenViewModel
@Inject
constructor(
    private val repository: AppRepository,
    private val loadingStateDelegate: LoadingStateDelegate,
) : ViewModel() {

    val isLoading: StateFlow<Boolean> = loadingStateDelegate.isLoading

    fun logout() {
        viewModelScope.launch { repository.logout() }
    }

    fun clearUserData(onSuccess: () -> Unit) {
        viewModelScope.launch {
            loadingStateDelegate.setLoading(true)
            val result = repository.clearUserData()
            if (result.isSuccess) {
                onSuccess()
            }
            loadingStateDelegate.setLoading(false)
        }
    }
}
