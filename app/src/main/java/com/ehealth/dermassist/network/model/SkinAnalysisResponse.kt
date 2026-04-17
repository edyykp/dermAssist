package com.ehealth.dermassist.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** Root response from the Perfect Corp AI Skin Analysis API. */
@JsonClass(generateAdapter = true)
data class SkinAnalysisResponse(
    @Json(name = "data") val data: AnalysisData,
    @Json(name = "status") val status: Int? = null,
)

/** Data wrapper containing the task status and the actual analysis results. */
@JsonClass(generateAdapter = true)
data class AnalysisData(
    @Json(name = "task_id") val taskId: String? = null,
    @Json(name = "task_status") val taskStatus: String,
    @Json(name = "results") val results: AnalysisResults? = null,
)

/** Container for the list of analysis output items and top-level summaries. */
@JsonClass(generateAdapter = true)
data class AnalysisResults(
    @Json(name = "output") val output: List<AnalysisOutputItem>? = null,
    @Json(name = "skin_type") val skinType: SkinType? = null,
    @Json(name = "skin_age") val skinAge: Int? = null,
    @Json(name = "skin_health_score") val skinHealthScore: Int? = null,
)

/**
 * Represents a specific skin concern result (e.g., acne, wrinkles). Includes scores, regions, and
 * optional segmentation mask URLs.
 */
@JsonClass(generateAdapter = true)
data class AnalysisOutputItem(
    @Json(name = "type") val type: String,
    @Json(name = "region") val region: String? = null,
    @Json(name = "raw_score") val rawScore: Double? = null,
    @Json(name = "ui_score") val uiScore: Int? = null,
    @Json(name = "score") val score: Double? = null,
    @Json(name = "mask_urls") val maskUrls: List<String>? = null,
    @Json(name = "severity") val severity: Int? = null,
    @Json(name = "points") val points: List<List<Int>>? = null,
)

@JsonClass(generateAdapter = true)
data class SkinType(
    @Json(name = "type") val type: Int,
    @Json(name = "type_name") val typeName: String,
)

// Extension functions to easily query the output list
fun SkinAnalysisResponse.getScoreByType(type: String, region: String = "whole"): Double? {
    val item = data.results?.output?.find { it.type == type && it.region == region }
    return item?.uiScore?.toDouble() ?: item?.rawScore ?: item?.score
}

fun SkinAnalysisResponse.getMaskUrlByType(type: String, region: String = "whole"): String? {
    return data.results
        ?.output
        ?.find { it.type == type && it.region == region }
        ?.maskUrls
        ?.firstOrNull()
}

fun SkinAnalysisResponse.getSkinAge(): Int {
    return data.results?.skinAge
        ?: data.results?.output?.find { it.type == "skin_age" }?.score?.toInt()
        ?: 0
}

fun SkinAnalysisResponse.getOverallScore(): Double? {
    return data.results?.skinHealthScore?.toDouble()
        ?: data.results?.output?.find { it.type == "all" }?.score
}
