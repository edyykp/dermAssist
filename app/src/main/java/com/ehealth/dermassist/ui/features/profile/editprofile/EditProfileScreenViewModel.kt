package com.ehealth.dermassist.ui.features.profile.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehealth.dermassist.domain.repository.AppRepository
import com.ehealth.dermassist.ui.LoadingStateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EditProfileScreenViewModel
@Inject
constructor(
    private val repository: AppRepository,
    private val loadingStateDelegate: LoadingStateDelegate,
) : ViewModel() {

    val isLoading: StateFlow<Boolean> = loadingStateDelegate.isLoading

    fun saveChanges(name: String, age: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            loadingStateDelegate.setLoading(true)
            val result = repository.updateUser(name, age)
            if (result.isSuccess) {
                onSuccess()
            }
            loadingStateDelegate.setLoading(false)
        }
    }
}
