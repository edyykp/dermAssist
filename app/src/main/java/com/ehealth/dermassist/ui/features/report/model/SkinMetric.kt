package com.ehealth.dermassist.ui.features.report.model

import androidx.compose.ui.graphics.Color

data class SkinMetric(
    val name: String,
    val value: Int, // 0–100
    val color: Color,
)
