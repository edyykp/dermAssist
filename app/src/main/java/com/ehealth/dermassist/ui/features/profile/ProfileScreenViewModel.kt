package com.ehealth.dermassist.ui.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehealth.dermassist.domain.repository.AppRepository
import com.ehealth.dermassist.domain.repository.ScanRepository
import com.ehealth.dermassist.ui.LoadingStateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileScreenViewModel
@Inject
constructor(
    private val repository: AppRepository,
    private val scanRepository: ScanRepository,
    private val loadingStateDelegate: LoadingStateDelegate,
) : ViewModel() {

    val isLoading: StateFlow<Boolean> = loadingStateDelegate.isLoading

    private val _userId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalScans: StateFlow<Int> =
        _userId
            .flatMapLatest { userId ->
                if (userId != null) {
                    scanRepository.getTotalScans(userId)
                } else {
                    flowOf(0)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0,
            )

    fun setUserId(id: String) {
        _userId.value = id
    }

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
