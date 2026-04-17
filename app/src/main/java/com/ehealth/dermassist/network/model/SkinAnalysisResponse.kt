package com.ehealth.dermassist.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SkinAnalysisResponse(
    @Json(name = "data") val data: AnalysisData,
    @Json(name = "status") val status: String? = null
)

@JsonClass(generateAdapter = true)
data class AnalysisData(
    @Json(name = "task_id") val taskId: String,
    @Json(name = "task_status") val taskStatus: String,
    @Json(name = "results") val results: AnalysisResults? = null
)

@JsonClass(generateAdapter = true)
data class AnalysisResults(
    @Json(name = "score") val overallScore: Int? = null,
    @Json(name = "skin_type") val skinType: String? = null,
    @Json(name = "conditions") val conditions: List<ConditionResult>? = null,
    @Json(name = "metrics") val metrics: Map<String, Int>? = null
)

@JsonClass(generateAdapter = true)
data class ConditionResult(
    @Json(name = "label") val label: String,
    @Json(name = "severity") val severity: String
)
