package com.ehealth.dermassist.ui.features.splash

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehealth.dermassist.R
import com.ehealth.dermassist.ui.features.auth.AuthViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

class SplashScreenViewModel : ViewModel() {

    fun handleGoogleSignIn(
        context: Context,
        authViewModel: AuthViewModel,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            try {
                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )
                val credential = result.credential
                if (credential is GoogleIdTokenCredential) {
                    authViewModel.signInWithGoogle(credential.idToken) { loginResult ->
                        if (loginResult.isSuccess) {
                            onSuccess()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Auth", "Google Sign In failed", e)
            }
        }
    }
}
