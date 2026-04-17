package com.ehealth.dermassist.data.model

/** Firestore entity representing a skin scan result. */
data class ScanEntity(
    val id: String = "",
    val userId: String = "",
    val createdAt: Long = 0,
    val scanArea: String = "",
    val overallScore: Int = 0,
    val skinAge: Int = 0,
    val skinType: String = "",
    val imageUrl: String = "",
    val conditions: List<ConditionEntity> = emptyList(),
    val metrics: List<MetricEntity> = emptyList(),
    val recommendations: List<RecommendationEntity> = emptyList(),
)

data class ConditionEntity(
    val label: String = "",
    val score: Int = 0,
    val region: String = "",
    val maskUrl: String? = null,
)

data class MetricEntity(val name: String = "", val value: Int = 0, val colorHex: String = "")

data class RecommendationEntity(
    val title: String = "",
    val description: String = "",
    val iconName: String = "",
    val iconBgHex: String = "",
    val iconTintHex: String = "",
)
