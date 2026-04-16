package com.ehealth.dermassist.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ScanEntity(
    val id: String = "",
    val userId: String = "",
    val createdAt: Long,
    val scanArea: String = "",
    val overallScore: Int = 0,
    val conditions: List<String> = emptyList(),
    val imageUrl: String = "",
    val metrics: List<Metric> = emptyList(),
    val recommendations: List<Recommendation> = emptyList(),
)

data class Metric(
    val name: String,
    val value: Int, // 0–100
    val color: Color,
)

data class Recommendation(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
)
