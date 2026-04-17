package com.ehealth.dermassist.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SkinAnalysisRequest(
    @Json(name = "src_file_id") val srcFileId: String? = null,
    @Json(name = "src_file_url") val srcFileUrl: String? = null,
    @Json(name = "dst_actions") val dstActions: List<String> = emptyList(),
    @Json(name = "miniserver_args") val miniserverArgs: MiniServerArgs = MiniServerArgs(),
    @Json(name = "format") val format: String = "json",
    @Json(name = "pf_camera_kit") val pfCameraKit: Boolean = false,
)

@JsonClass(generateAdapter = true)
data class MiniServerArgs(
    @Json(name = "enable_mask_overlay") val enableMaskOverlay: Boolean = false
)
