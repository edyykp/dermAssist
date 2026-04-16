package com.ehealth.dermassist.ui.features.history

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
import com.ehealth.dermassist.ui.LoadingStateDelegate
import com.ehealth.dermassist.ui.features.report.model.ConditionSeverity
import com.ehealth.dermassist.ui.features.report.model.SkinCondition
import com.ehealth.dermassist.ui.features.report.model.SkinMetric
import com.ehealth.dermassist.ui.features.report.model.SkinRecommendation
import com.ehealth.dermassist.ui.features.report.model.SkinReport
import com.ehealth.dermassist.ui.theme.getConditionColors
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ScanDetailViewModel
@Inject
constructor(
    private val scanRepository: ScanRepository,
    private val loadingStateDelegate: LoadingStateDelegate,
) : ViewModel() {
    private val _scanReport = MutableStateFlow<SkinReport?>(null)
    val scanReport: StateFlow<SkinReport?> = _scanReport.asStateFlow()

    fun loadScanDetail(userId: String, scanId: String) {
        if (userId.isBlank() || scanId.isBlank()) return

        viewModelScope.launch {
            loadingStateDelegate.setLoading(true)
            val entity = scanRepository.getScanDetails(userId, scanId)
            _scanReport.value =
                entity?.let {
                    SkinReport(
                        scanDate = formatFullDate(it.createdAt),
                        scanArea = it.scanArea,
                        overallScore = it.overallScore,
                        conditions =
                            it.conditions.map { label ->
                                val (bg, text) = getConditionColors(label)
                                SkinCondition(
                                    label = label,
                                    severity = ConditionSeverity.GOOD,
                                    bgColor = bg,
                                    textColor = text,
                                )
                            },
                        metrics =
                            it.metrics.map { metric ->
                                SkinMetric(
                                    name = metric.name,
                                    value = metric.value,
                                    color = parseColor(metric.colorHex),
                                )
                            },
                        recommendations =
                            it.recommendations.map { recommendation ->
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
            loadingStateDelegate.setLoading(false)
        }
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
