package com.ehealth.dermassist.ui.features.home

import android.app.Application
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ehealth.dermassist.network.SkinAnalysisRepository
import com.ehealth.dermassist.ui.LoadingStateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    application: Application,
    private val loadingStateDelegate: LoadingStateDelegate,
    private val skinAnalysisRepository: SkinAnalysisRepository,
) : AndroidViewModel(application) {

    private val TAG = "HomeViewModel"

    // Limits based on Perfect Corp S2S API documentation for HD Skincare
    private val MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024 // 10MB
    private val SUPPORTED_MIME_TYPES = listOf("image/jpeg", "image/png")
    private val MAX_LONG_SIDE = 4096
    private val MIN_SHORT_SIDE = 1080

    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents: SharedFlow<String> = _errorEvents.asSharedFlow()

    fun processImage(uri: Uri?) {
        if (uri == null) return

        viewModelScope.launch {
            loadingStateDelegate.setLoading(true)

            try {
                val contentResolver = getApplication<Application>().contentResolver

                // 1. Validate File Type
                val contentType = contentResolver.getType(uri) ?: "image/jpeg"
                if (!SUPPORTED_MIME_TYPES.contains(contentType)) {
                    _errorEvents.emit("Unsupported file format. Please use JPG or PNG.")
                    loadingStateDelegate.setLoading(false)
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
                    loadingStateDelegate.setLoading(false)
                    return@launch
                }

                // 4. Validate File Size
                if (bytes.size > MAX_FILE_SIZE_BYTES) {
                    _errorEvents.emit("Image is too large (Max 10MB).")
                    loadingStateDelegate.setLoading(false)
                    return@launch
                }

                // 5. Validate Dimensions (HD Skincare specs)
                val options =
                    BitmapFactory.Options().apply {
                        inJustDecodeBounds = true // Don't load full bitmap into memory
                    }
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

                val width = options.outWidth
                val height = options.outHeight
                val longSide = max(width, height)
                val shortSide = min(width, height)

                if (longSide > MAX_LONG_SIDE || shortSide < MIN_SHORT_SIDE) {
                    _errorEvents.emit(
                        "Image dimensions out of range for HD analysis.\n" +
                            "Required: Long side ≤ 4096px, Short side ≥ 1080px.\n" +
                            "Detected: ${width}x${height}px"
                    )
                    loadingStateDelegate.setLoading(false)
                    return@launch
                }

                // 6. Upload file to Perfect Corp API
                val uploadResult =
                    skinAnalysisRepository.uploadFile(
                        imageBytes = bytes,
                        fileName = fileName,
                        contentType = contentType,
                    )

                uploadResult
                    .onSuccess { fileId ->
                        Log.d(TAG, "File uploaded successfully. FileId: $fileId")

                        // 7. Start analysis using the returned fileId
                        val analysisResult = skinAnalysisRepository.startAnalysis(fileId)

                        analysisResult
                            .onSuccess { response ->
                                Log.d(TAG, "Analysis completed successfully")
                                // TODO: Map 'response' to your ScanEntity and save to Firestore
                            }
                            .onFailure { error ->
                                Log.e(TAG, "Analysis failed: ${error.message}", error)
                                _errorEvents.emit("Analysis failed: ${error.message}")
                            }
                    }
                    .onFailure { error ->
                        Log.e(TAG, "Upload failed: ${error.message}", error)
                        _errorEvents.emit("Upload failed: ${error.message}")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error processing image", e)
                _errorEvents.emit("An unexpected error occurred.")
            } finally {
                loadingStateDelegate.setLoading(false)
            }
        }
    }
}
