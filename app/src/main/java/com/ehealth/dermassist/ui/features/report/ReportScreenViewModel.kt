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
import com.ehealth.dermassist.ui.LoadingStateDelegate
import com.ehealth.dermassist.ui.features.report.model.ConditionSeverity
import com.ehealth.dermassist.ui.features.report.model.SkinCondition
import com.ehealth.dermassist.ui.features.report.model.SkinMetric
import com.ehealth.dermassist.ui.features.report.model.SkinRecommendation
import com.ehealth.dermassist.ui.features.report.model.SkinReport
import com.ehealth.dermassist.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@HiltViewModel
class ReportScreenViewModel
@Inject
constructor(
    private val scanRepository: ScanRepository,
    private val loadingStateDelegate: LoadingStateDelegate,
) : ViewModel() {

    val isLoading: StateFlow<Boolean> = loadingStateDelegate.isLoading

    private val _userId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val latestScan: StateFlow<SkinReport?> =
        _userId
            .filterNotNull()
            .flatMapLatest { userId ->
                scanRepository
                    .getUserScans(userId)
                    .onStart { loadingStateDelegate.setLoading(true) }
                    .onEach { loadingStateDelegate.setLoading(false) }
                    .map { entities ->
                        entities.firstOrNull()?.let { entity ->
                            SkinReport(
                                scanDate = formatFullDate(entity.createdAt),
                                scanArea = entity.scanArea,
                                overallScore = entity.overallScore,
                                imageUrl = entity.conditions[0].maskUrl ?: "",
                                skinAge = entity.skinAge,
                                skinType = entity.skinType,
                                conditions =
                                    entity.conditions.map { cond ->
                                        val (bg, text) = getConditionColors(cond.label)
                                        SkinCondition(
                                            label = cond.label,
                                            score = cond.score,
                                            region = cond.region,
                                            severity = mapScoreToSeverity(cond.score),
                                            bgColor = bg,
                                            textColor = text,
                                        )
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

    private fun mapScoreToSeverity(score: Int): ConditionSeverity {
        return when {
            score >= 80 -> ConditionSeverity.GOOD
            score >= 60 -> ConditionSeverity.MODERATE
            else -> ConditionSeverity.CONCERN
        }
    }
}
