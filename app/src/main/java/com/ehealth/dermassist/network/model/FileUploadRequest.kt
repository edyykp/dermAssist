package com.ehealth.dermassist.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FileUploadRequest(@Json(name = "files") val files: List<FileRequestItem>)

@JsonClass(generateAdapter = true)
data class FileRequestItem(
    @Json(name = "content_type") val contentType: String,
    @Json(name = "file_name") val fileName: String,
    @Json(name = "file_size") val fileSize: Int,
)
