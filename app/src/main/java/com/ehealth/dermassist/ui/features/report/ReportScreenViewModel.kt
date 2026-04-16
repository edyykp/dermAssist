package com.ehealth.dermassist.ui.features.report

import androidx.lifecycle.ViewModel
import com.ehealth.dermassist.domain.model.ScanHistoryItem
import com.ehealth.dermassist.domain.repository.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

@HiltViewModel
class ReportScreenViewModel @Inject constructor(scanRepository: ScanRepository) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val latestScan: Flow<ScanHistoryItem?> =
        scanRepository
            .getUserScans("userId")
            .onStart { _isLoading.value = true }
            .map { it.firstOrNull() }
            .onEach { _isLoading.value = false }
}
