package com.ehealth.dermassist.data.model

data class ScanEntity(
    val id: String = "",
    val userId: String = "",
    val createdAt: Long = 0,
    val scanArea: String = "",
    val overallScore: Int = 0,
    val conditions: List<String> = emptyList(),
    val imageUrl: String = "",
    val metrics: List<MetricEntity> = emptyList(),
    val recommendations: List<RecommendationEntity> = emptyList(),
)

data class MetricEntity(val name: String = "", val value: Int = 0, val colorHex: String = "")

data class RecommendationEntity(
    val title: String = "",
    val description: String = "",
    val iconName: String = "",
    val iconBgHex: String = "",
    val iconTintHex: String = "",
)
