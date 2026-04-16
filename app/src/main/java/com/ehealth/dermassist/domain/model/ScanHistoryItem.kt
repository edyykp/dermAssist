package com.ehealth.dermassist.domain.model

import androidx.compose.ui.graphics.Color

data class ScanHistoryItem(
    val id: String,
    val date: String,
    val time: String,
    val scanTitle: String,
    val conditions: List<HistoryConditionTag>,
    val accentColor: Color,
    val accentBgColor: Color,
)
