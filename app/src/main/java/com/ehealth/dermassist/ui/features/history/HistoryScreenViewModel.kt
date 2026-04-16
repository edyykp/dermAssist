package com.ehealth.dermassist.ui.features.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehealth.dermassist.domain.model.HistoryConditionTag
import com.ehealth.dermassist.domain.model.ScanHistoryItem
import com.ehealth.dermassist.domain.repository.ScanRepository
import com.ehealth.dermassist.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@HiltViewModel
class HistoryScreenViewModel @Inject constructor(private val scanRepository: ScanRepository) :
    ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _scans = MutableStateFlow<List<ScanHistoryItem>>(emptyList())
    val scans: StateFlow<List<ScanHistoryItem>> = _scans.asStateFlow()

    fun start(userId: String) {
        observeScans(userId)
    }

    private fun observeScans(userId: String) {
        viewModelScope.launch {
            scanRepository
                .getUserScans(userId)
                .onStart { _isLoading.value = true }
                .map { entities ->
                    entities.map { entity ->
                        val (date, time) = formatDateTime(entity.createdAt)
                        ScanHistoryItem(
                            id = entity.id,
                            date = date,
                            time = time,
                            scanTitle = entity.scanArea,
                            conditions =
                                entity.conditions.map {
                                    HistoryConditionTag(it, IconBgGreen, PrimaryGreen)
                                },
                            accentColor = PrimaryGreen,
                            accentBgColor = IconBgGreen,
                        )
                    }
                }
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

    private fun formatDateTime(timestamp: Long): Pair<String, String> {
        val dateObj = java.util.Date(timestamp)

        val date = java.text.SimpleDateFormat("MMM dd", java.util.Locale.US).format(dateObj)

        val time = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.US).format(dateObj)

        return date to time
    }
}
