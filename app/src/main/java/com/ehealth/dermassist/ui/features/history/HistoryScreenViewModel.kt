package com.ehealth.dermassist.ui.features.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehealth.dermassist.domain.model.HistoryConditionTag
import com.ehealth.dermassist.domain.model.ScanHistoryItem
import com.ehealth.dermassist.domain.repository.ScanRepository
import com.ehealth.dermassist.ui.LoadingStateDelegate
import com.ehealth.dermassist.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@HiltViewModel
class HistoryScreenViewModel
@Inject
constructor(
    private val scanRepository: ScanRepository,
    private val loadingStateDelegate: LoadingStateDelegate,
) : ViewModel() {
    private val _scans = MutableStateFlow<List<ScanHistoryItem>>(emptyList())
    val scans: StateFlow<List<ScanHistoryItem>> = _scans.asStateFlow()

    private var observeJob: Job? = null

    fun start(userId: String) {
        if (userId.isBlank()) return

        // Cancel previous observation if userId changes
        observeJob?.cancel()
        observeJob =
            viewModelScope.launch {
                scanRepository
                    .getUserScans(userId)
                    .onStart { loadingStateDelegate.setLoading(true) }
                    .map { entities ->
                        entities.map { entity ->
                            val (date, time) = formatDateTime(entity.createdAt)
                            ScanHistoryItem(
                                id = entity.id,
                                date = date,
                                time = time,
                                scanTitle = entity.scanArea,
                                conditions =
                                    entity.conditions.map { label ->
                                        val (bg, text) = getConditionColors(label)
                                        HistoryConditionTag(label, bg, text)
                                    },
                                accentColor = PrimaryGreen,
                                accentBgColor = IconBgGreen,
                            )
                        }
                    }
                    .catch {
                        _scans.value = emptyList()
                        loadingStateDelegate.setLoading(false)
                    }
                    .collect { list ->
                        _scans.value = list
                        loadingStateDelegate.setLoading(false)
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
