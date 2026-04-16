package com.ehealth.dermassist.ui.features.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehealth.dermassist.domain.repository.ScanRepository
import com.ehealth.dermassist.ui.features.report.model.ConditionSeverity
import com.ehealth.dermassist.ui.features.report.model.SkinCondition
import com.ehealth.dermassist.ui.features.report.model.SkinMetric
import com.ehealth.dermassist.ui.features.report.model.SkinRecommendation
import com.ehealth.dermassist.ui.features.report.model.SkinReport
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@HiltViewModel
class ReportScreenViewModel @Inject constructor(private val scanRepository: ScanRepository) :
    ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val latestScan: StateFlow<SkinReport?> =
        _userId
            .filterNotNull()
            .flatMapLatest { userId ->
                scanRepository
                    .getUserScans(userId)
                    .onStart { _isLoading.value = true }
                    .onEach { _isLoading.value = false }
                    .map { entities ->
                        entities.firstOrNull()?.let { entity ->
                            SkinReport(
                                scanDate = formatFullDate(entity.createdAt),
                                scanArea = entity.scanArea,
                                overallScore = entity.overallScore,
                                conditions =
                                    entity.conditions.map {
                                        SkinCondition(it, ConditionSeverity.GOOD)
                                    },
                                metrics =
                                    entity.metrics.map { metric ->
                                        SkinMetric(
                                            name = metric.name,
                                            value = metric.value,
                                            color = metric.color,
                                        )
                                    },
                                recommendations =
                                    entity.recommendations.map { recommendation ->
                                        SkinRecommendation(
                                            title = recommendation.title,
                                            description = recommendation.description,
                                            icon = recommendation.icon,
                                            iconBg = recommendation.iconBg,
                                            iconTint = recommendation.iconTint,
                                        )
                                    },
                            )
                        }
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null,
            )

    fun setUserId(id: String) {
        _userId.value = id
    }

    private fun formatFullDate(timestamp: Long): String {
        return java.text
            .SimpleDateFormat("MMM dd, yyyy", java.util.Locale.US)
            .format(java.util.Date(timestamp))
    }
}
