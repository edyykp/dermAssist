package com.ehealth.dermassist.ui.features.report.model

data class SkinReport(
    val scanDate: String,
    val scanArea: String,
    val overallScore: Int,
    val imageUrl: String = "",
    val skinAge: Int? = null,
    val skinType: String = "",
    val conditions: List<SkinCondition>,
    val metrics: List<SkinMetric>,
    val recommendations: List<SkinRecommendation>,
)
