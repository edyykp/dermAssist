package com.ehealth.dermassist.network

import android.util.Log
import com.ehealth.dermassist.BuildConfig
import com.ehealth.dermassist.network.model.MiniServerArgs
import com.ehealth.dermassist.network.model.SkinAnalysisRequest
import com.ehealth.dermassist.network.model.SkinAnalysisResponse
import kotlinx.coroutines.delay
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SkinAnalysisRepository @Inject constructor(
    private val service: SkinAnalysisService
) {
    private val TAG = "SkinAnalysisRepo"
    private val authHeader = "Bearer ${BuildConfig.SKIN_ANALYSIS_API_KEY}"

    suspend fun analyzeSkin(imageUrl: String): Result<SkinAnalysisResponse> {
        return try {
            val request = SkinAnalysisRequest(
                srcFileUrl = imageUrl,
                dstActions = listOf(
                    "hd_acne",
                    "hd_droopy_lower_eyelid",
                    "hd_eye_bag",
                    "hd_moisture",
                    "hd_pore",
                    "hd_redness",
                    "hd_texture",
                    "hd_dark_circle",
                    "hd_droopy_upper_eyelid",
                    "hd_firmness",
                    "hd_oiliness",
                    "hd_radiance",
                    "hd_wrinkle",
                    "hd_age_spot",
                ),
                miniserverArgs = MiniServerArgs(
                    enableMaskOverlay = true
                )
            )
            val startResponse: Response<SkinAnalysisResponse> = service.startAnalysis(authHeader, request)
            
            if (!startResponse.isSuccessful) {
                return Result.failure(Exception("Failed to start analysis: ${startResponse.code()}"))
            }

            val taskId = startResponse.body()?.data?.taskId 
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
                val response: Response<SkinAnalysisResponse> = service.getAnalysisResult(authHeader, taskId)
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    when (data?.taskStatus) {
                        "success" -> return Result.success(response.body()!!)
                        "error" -> return Result.failure(Exception("Analysis failed on server"))
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
