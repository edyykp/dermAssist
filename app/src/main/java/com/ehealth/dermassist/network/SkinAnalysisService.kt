package com.ehealth.dermassist.network

import com.ehealth.dermassist.network.model.FileUploadRequest
import com.ehealth.dermassist.network.model.FileUploadResponse
import com.ehealth.dermassist.network.model.SkinAnalysisRequest
import com.ehealth.dermassist.network.model.SkinAnalysisResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/** Retrofit service interface for the Skin Analysis API. */
interface SkinAnalysisService {

    @POST("file/skin-analysis")
    suspend fun requestUploadUrl(
        @Header("Authorization") auth: String,
        @Body request: FileUploadRequest,
    ): Response<FileUploadResponse>

    @PUT
    suspend fun uploadToPresignedUrl(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @Body file: RequestBody,
    ): Response<Unit>

    @POST("task/skin-analysis")
    suspend fun startAnalysis(
        @Header("Authorization") auth: String,
        @Body request: SkinAnalysisRequest,
    ): Response<SkinAnalysisResponse>

    @GET("task/skin-analysis/{taskId}")
    suspend fun getAnalysisResult(
        @Header("Authorization") auth: String,
        @Path("taskId") taskId: String,
    ): Response<SkinAnalysisResponse>
}
