package com.ehealth.dermassist.ui.features.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehealth.dermassist.domain.model.ScanHistoryItem
import com.ehealth.dermassist.domain.repository.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@HiltViewModel
class HistoryScreenViewModel @Inject constructor(private val scanRepository: ScanRepository) :
    ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _scans = MutableStateFlow<List<ScanHistoryItem>>(emptyList())
    val scans: StateFlow<List<ScanHistoryItem>> = _scans.asStateFlow()

    // ─────────────────────────────
    // START OBSERVING EVERYTHING
    // ─────────────────────────────
    fun start(userId: String) {
        observeScans(userId)
    }

    // ─────────────────────────────
    // SCANS LIST (REAL-TIME)
    // ─────────────────────────────
    private fun observeScans(userId: String) {
        viewModelScope.launch {
            scanRepository
                .getUserScans(userId)
                .onStart { _isLoading.value = true }
                .catch {
                    _scans.value = emptyList()
                    _isLoading.value = false
                }
                .collect { list ->
                    _scans.value = list
                    _isLoading.value = false
                }
        }
    }
}
