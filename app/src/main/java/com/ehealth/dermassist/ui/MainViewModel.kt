package com.ehealth.dermassist.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class MainViewModel @Inject constructor(private val loadingStateDelegate: LoadingStateDelegate) :
    ViewModel() {
    val isLoading: StateFlow<Boolean> = loadingStateDelegate.isLoading
}
