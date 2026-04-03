package com.ehealth.dermassist.domain.usecase

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import com.ehealth.dermassist.R
import com.ehealth.dermassist.domain.repository.AppRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(private val repository: AppRepository) {
    suspend operator fun invoke(context: Context): Result<Unit> {
        val credentialManager = CredentialManager.create(context)
        val webClientId = context.getString(R.string.default_web_client_id)

        val googleIdOption: GetGoogleIdOption =
            GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

        val request: GetCredentialRequest =
            GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

        return try {
            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                repository.signInWithGoogle(googleIdTokenCredential.idToken)
            } else {
                Result.failure(Exception("Unexpected credential type: ${credential.type}"))
            }
        } catch (e: Exception) {
            if (e is NoCredentialException) {
                val intent =
                    Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                        putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                context.startActivity(intent)
            }
            Result.failure(e)
        }
    }
}
