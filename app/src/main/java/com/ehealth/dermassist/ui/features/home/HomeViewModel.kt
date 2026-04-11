package com.ehealth.dermassist.ui.features.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehealth.dermassist.ui.LoadingStateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(private val loadingStateDelegate: LoadingStateDelegate) :
    ViewModel() {

    fun processImage(uri: Uri?) {
        if (uri == null) return

        viewModelScope.launch {
            loadingStateDelegate.setLoading(true)

            // Simulating image upload and processing
            // In a real app, you'd use a repository to send the file to a URL
            delay(3000)

            loadingStateDelegate.setLoading(false)
        }
    }
}
