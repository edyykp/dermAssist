package com.ehealth.dermassist.ui.features.report

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.graphics.toColorInt
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
                                            color = parseColor(metric.colorHex),
                                        )
                                    },
                                recommendations =
                                    entity.recommendations.map { recommendation ->
                                        SkinRecommendation(
                                            title = recommendation.title,
                                            description = recommendation.description,
                                            icon = parseIcon(recommendation.iconName),
                                            iconBg = parseColor(recommendation.iconBgHex),
                                            iconTint = parseColor(recommendation.iconTintHex),
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

    private fun parseColor(hex: String): Color {
        return try {
            if (hex.startsWith("0x")) {
                Color(hex.removePrefix("0x").toLong(16))
            } else {
                Color(hex.toColorInt())
            }
        } catch (_: Exception) {
            Color.Gray
        }
    }

    private fun parseIcon(name: String): ImageVector {
        return when (name) {
            "wb_sunny" -> Icons.Outlined.WbSunny
            "warning" -> Icons.Outlined.Warning
            "check_circle" -> Icons.Outlined.CheckCircle
            else -> Icons.Outlined.Info
        }
    }
}
