package com.ehealth.dermassist.ui.features.splash

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehealth.dermassist.R
import com.ehealth.dermassist.domain.repository.AppRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SplashScreenViewModel @Inject constructor(private val repository: AppRepository) :
    ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun handleGoogleSignIn(context: Context, onSuccess: () -> Unit) {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            val credentialManager = CredentialManager.create(context)

            val webClientId = context.getString(R.string.default_web_client_id)

            val googleIdOption: GetGoogleIdOption =
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .build()

            val request: GetCredentialRequest =
                GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

            try {
                val result = credentialManager.getCredential(context = context, request = request)
                val credential = result.credential

                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val loginResult =
                            repository.signInWithGoogle(googleIdTokenCredential.idToken)
                        if (loginResult.isSuccess) {
                            onSuccess()
                        }
                    } catch (e: Exception) {
                        Log.e("SplashScreenViewModel", "Error creating GoogleIdTokenCredential", e)
                    }
                }
            } catch (e: Exception) {
                Log.e("SplashScreenViewModel", "Error getting credential", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
