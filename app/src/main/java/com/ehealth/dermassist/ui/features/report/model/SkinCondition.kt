package com.ehealth.dermassist.ui.features.report.model

import androidx.compose.ui.graphics.Color

data class SkinCondition(
    val label: String,
    val severity: ConditionSeverity,
    val bgColor: Color,
    val textColor: Color,
)
