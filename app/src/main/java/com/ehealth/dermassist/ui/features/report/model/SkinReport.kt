package com.ehealth.dermassist.ui.features.report.model

data class SkinReport(
    val scanDate: String,
    val scanArea: String,
    val overallScore: Int,
    val conditions: List<SkinCondition>,
    val metrics: List<SkinMetric>,
    val recommendations: List<SkinRecommendation>,
)
