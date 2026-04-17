package com.ehealth.dermassist.ui.features.home

import android.app.Application
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ehealth.dermassist.data.model.ConditionEntity
import com.ehealth.dermassist.data.model.MetricEntity
import com.ehealth.dermassist.data.model.ScanEntity
import com.ehealth.dermassist.domain.repository.AppRepository
import com.ehealth.dermassist.domain.repository.ScanRepository
import com.ehealth.dermassist.network.SkinAnalysisRepository
import com.ehealth.dermassist.network.model.SkinAnalysisResponse
import com.ehealth.dermassist.network.model.getOverallScore
import com.ehealth.dermassist.network.model.getSkinAge
import com.ehealth.dermassist.ui.LoadingStateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    application: Application,
    private val loadingStateDelegate: LoadingStateDelegate,
    private val skinAnalysisRepository: SkinAnalysisRepository,
    private val scanRepository: ScanRepository,
    private val appRepository: AppRepository,
) : AndroidViewModel(application) {

    private val TAG = "HomeViewModel"

    private val MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024 // 10MB
    private val SUPPORTED_MIME_TYPES = listOf("image/jpeg", "image/png")
    private val MAX_LONG_SIDE = 4096
    private val MIN_SHORT_SIDE = 480

    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents: SharedFlow<String> = _errorEvents.asSharedFlow()

    private val _scanSuccessEvent = MutableSharedFlow<Unit>()
    val scanSuccessEvent: SharedFlow<Unit> = _scanSuccessEvent.asSharedFlow()

    fun processImage(uri: Uri?) {
        if (uri == null) return

        viewModelScope.launch {
            loadingStateDelegate.setLoading(true)

            try {
                val user = appRepository.getUser().first()
                if (user == null) {
                    _errorEvents.emit("User not logged in.")
                    return@launch
                }

                val contentResolver = getApplication<Application>().contentResolver

                // 1. Validate File Type
                val contentType = contentResolver.getType(uri) ?: "image/jpeg"
                if (!SUPPORTED_MIME_TYPES.contains(contentType)) {
                    _errorEvents.emit("Unsupported file format. Please use JPG or PNG.")
                    return@launch
                }

                // 2. Resolve extension and file name
                val extension =
                    MimeTypeMap.getSingleton().getExtensionFromMimeType(contentType) ?: "jpg"
                val fileName = "scan_${System.currentTimeMillis()}.$extension"

                // 3. Read bytes
                val inputStream = contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes == null) {
                    _errorEvents.emit("Failed to read image data.")
                    return@launch
                }

                // 4. Validate File Size
                if (bytes.size > MAX_FILE_SIZE_BYTES) {
                    _errorEvents.emit("Image is too large (Max 10MB).")
                    return@launch
                }

                // 5. Validate Dimensions
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

                if (options.outWidth <= 0 || options.outHeight <= 0) {
                    _errorEvents.emit("Invalid image data.")
                    return@launch
                }

                val longSide = max(options.outWidth, options.outHeight)
                val shortSide = min(options.outWidth, options.outHeight)

                Log.d(TAG, "Detected dimensions: ${options.outWidth}x${options.outHeight}")

                if (longSide > MAX_LONG_SIDE || shortSide < MIN_SHORT_SIDE) {
                    _errorEvents.emit(
                        "Image quality insufficient for analysis.\n" +
                            "Required: Long side ≤ 4096px, Short side ≥ 480px.\n" +
                            "Detected: ${options.outWidth}x${options.outHeight}px"
                    )
                    return@launch
                }

                // 6. Upload file
                val uploadResult = skinAnalysisRepository.uploadFile(bytes, fileName, contentType)

                uploadResult
                    .onSuccess { fileId ->
                        // 7. Start analysis
                        val analysisResult = skinAnalysisRepository.startAnalysis(fileId)

                        analysisResult
                            .onSuccess { response ->
                                // 8. Map to ScanEntity and Save
                                val scanEntity =
                                    mapResponseToEntity(user.id, uri.toString(), response)
                                val saveResult = scanRepository.addScan(scanEntity)

                                if (saveResult.isSuccess) {
                                    _scanSuccessEvent.emit(Unit)
                                } else {
                                    _errorEvents.emit("Failed to save scan results.")
                                }
                            }
                            .onFailure { error ->
                                _errorEvents.emit("Analysis failed: ${error.message}")
                            }
                    }
                    .onFailure { error -> _errorEvents.emit("Upload failed: ${error.message}") }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing image", e)
                _errorEvents.emit("An unexpected error occurred.")
            } finally {
                loadingStateDelegate.setLoading(false)
            }
        }
    }

    private fun mapResponseToEntity(
        userId: String,
        localUri: String,
        response: SkinAnalysisResponse,
    ): ScanEntity {
        val results = response.data.results

        val conditions =
            results?.output?.map {
                ConditionEntity(
                    label = it.type.replace("_", " ").replace(" v2", "").capitalize(),
                    score = it.uiScore ?: it.rawScore?.toInt() ?: it.score?.toInt() ?: 0,
                    region = it.region ?: "whole",
                    maskUrl = it.maskUrls?.firstOrNull(),
                )
            } ?: emptyList()

        return ScanEntity(
            userId = userId,
            createdAt = System.currentTimeMillis(),
            scanArea = "Face", // Default for SD Skincare API
            overallScore = response.getOverallScore()?.toInt() ?: 0,
            skinAge = response.getSkinAge(),
            skinType = results?.skinType?.typeName ?: "Unknown",
            imageUrl = localUri, // Ideally upload to Firebase Storage and use that URL
            conditions = conditions,
            metrics =
                listOf(
                    MetricEntity(
                        "Skin Health",
                        response.getOverallScore()?.toInt() ?: 0,
                        "#1A6E5C",
                    ),
                    MetricEntity("Skin Age", response.getSkinAge() ?: 0, "#3B7DD8"),
                ),
        )
    }

    private fun String.capitalize() = this.replaceFirstChar { it.uppercase() }
}
