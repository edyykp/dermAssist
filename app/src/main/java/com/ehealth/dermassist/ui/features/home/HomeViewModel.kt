package com.ehealth.dermassist.ui.features.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehealth.dermassist.network.SkinAnalysisRepository
import com.ehealth.dermassist.ui.LoadingStateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val loadingStateDelegate: LoadingStateDelegate,
    private val skinAnalysisRepository: SkinAnalysisRepository,
    ) :
    ViewModel() {

    fun processImage(uri: Uri?) {
        if (uri == null) return

        viewModelScope.launch {
            loadingStateDelegate.setLoading(true)

            val result = skinAnalysisRepository.analyzeSkin(uri.toString())
            result.onSuccess { response ->
                // Handle the full skin analysis data
                val score = response.data.results?.overallScore
            }.onFailure { error ->
                // Handle network or polling errors
            }

            loadingStateDelegate.setLoading(false)
        }
    }
}
