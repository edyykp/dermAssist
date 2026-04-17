package com.ehealth.dermassist.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FileUploadResponse(
    @Json(name = "status") val status: Int,
    @Json(name = "data") val data: FileUploadResponseData,
)

@JsonClass(generateAdapter = true)
data class FileUploadResponseData(@Json(name = "files") val files: List<FileResponseItem>)

@JsonClass(generateAdapter = true)
data class FileResponseItem(
    @Json(name = "content_type") val contentType: String,
    @Json(name = "file_name") val fileName: String,
    @Json(name = "file_id") val fileId: String,
    @Json(name = "requests") val requests: List<UploadRequestInfo>,
)

@JsonClass(generateAdapter = true)
data class UploadRequestInfo(
    @Json(name = "headers") val headers: Map<String, String>,
    @Json(name = "url") val url: String,
    @Json(name = "method") val method: String,
)
