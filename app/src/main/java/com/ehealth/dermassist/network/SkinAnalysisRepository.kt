package com.ehealth.dermassist.network

import android.util.Log
import com.ehealth.dermassist.BuildConfig
import com.ehealth.dermassist.network.model.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

@Singleton
class SkinAnalysisRepository @Inject constructor(private val service: SkinAnalysisService) {
    private val TAG = "SkinAnalysisRepo"
    private val authHeader = "Bearer ${BuildConfig.SKIN_ANALYSIS_API_KEY}"

    suspend fun uploadFile(
        imageBytes: ByteArray,
        fileName: String,
        contentType: String,
    ): Result<String> {
        return try {
            // 1. Request presigned upload URL
            val uploadRequest =
                FileUploadRequest(
                    files =
                        listOf(
                            FileRequestItem(
                                contentType = contentType,
                                fileName = fileName,
                                fileSize = imageBytes.size,
                            )
                        )
                )

            val urlResponse = service.requestUploadUrl(authHeader, uploadRequest)
            if (!urlResponse.isSuccessful) {
                return Result.failure(Exception("Failed to get upload URL: ${urlResponse.code()}"))
            }

            val fileResponseItem =
                urlResponse.body()?.data?.files?.firstOrNull()
                    ?: return Result.failure(Exception("No file info in upload URL response"))

            val uploadInfo =
                fileResponseItem.requests.firstOrNull()
                    ?: return Result.failure(Exception("No upload request info found"))

            // 2. Upload actual bytes to presigned URL
            val requestBody = imageBytes.toRequestBody(contentType.toMediaTypeOrNull())
            val putResponse =
                service.uploadToPresignedUrl(
                    url = uploadInfo.url,
                    headers = uploadInfo.headers,
                    file = requestBody,
                )

            if (putResponse.isSuccessful) {
                Result.success(fileResponseItem.fileId)
            } else {
                Result.failure(Exception("PUT upload failed: ${putResponse.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during file upload process", e)
            Result.failure(e)
        }
    }

    suspend fun startAnalysis(fileId: String): Result<SkinAnalysisResponse> {
        return try {
            val request =
                SkinAnalysisRequest(
                    srcFileId = fileId,
                    dstActions =
                        listOf(
                            "acne",
                            "droopy_lower_eyelid",
                            "eye_bag",
                            "moisture",
                            "pore",
                            "redness",
                            "texture",
                            "dark_circle_v2",
                            "droopy_upper_eyelid",
                            "firmness",
                            "oiliness",
                            "radiance",
                            "wrinkle",
                            "age_spot",
                        ),
                    miniserverArgs = MiniServerArgs(enableMaskOverlay = true),
                )
            val startResponse: Response<SkinAnalysisResponse> =
                service.startAnalysis(authHeader, request)

            if (!startResponse.isSuccessful) {
                return Result.failure(
                    Exception(
                        "Failed to start analysis: ${startResponse.code()} ${startResponse.message()}"
                    )
                )
            }

            val taskId =
                startResponse.body()?.data?.taskId
                    ?: return Result.failure(Exception("Task ID not found in response"))

            pollForResults(taskId)
        } catch (e: Exception) {
            Log.e(TAG, "Error during skin analysis start", e)
            Result.failure(e)
        }
    }

    private suspend fun pollForResults(taskId: String): Result<SkinAnalysisResponse> {
        repeat(30) { attempt ->
            Log.d(TAG, "Polling for results, attempt ${attempt + 1}")
            delay(2000)

            try {
                val response: Response<SkinAnalysisResponse> =
                    service.getAnalysisResult(authHeader, taskId)
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    when (data?.taskStatus) {
                        "success" -> return Result.success(response.body()!!)
                        "error" ->
                            return Result.failure(
                                Exception("Analysis failed on server: ${data.error}")
                            )
                    }
                } else {
                    Log.e(TAG, "Polling response failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during polling", e)
            }
        }
        return Result.failure(Exception("Polling timeout"))
    }
}
