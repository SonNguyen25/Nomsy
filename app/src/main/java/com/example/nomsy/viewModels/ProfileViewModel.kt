package com.example.nomsy.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.example.nomsy.data.local.UserDatabase
import com.example.nomsy.data.local.models.User
import com.example.nomsy.data.remote.UpdateProfileRequest
import com.example.nomsy.data.repository.AuthRepository
import com.example.nomsy.utils.Result
import kotlinx.coroutines.launch

class ProfileViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AuthRepository(userDatabase = UserDatabase.getDatabase(app))

    private val _profile = MutableLiveData<Result<User>>()
    val profile: LiveData<Result<User>> = _profile

    private val _updateResult = MutableLiveData<Result<User>?>()
    val updateResult: MutableLiveData<Result<User>?> = _updateResult

    fun fetchByUsername(username: String) = viewModelScope.launch {
        repo.getProfileByUsername(username).observeForever { _profile.postValue(it) }
    }

    fun updateProfile(username: String, req: UpdateProfileRequest) = viewModelScope.launch {
        repo.updateProfile(username, req).observeForever { result ->
            _updateResult.postValue(result)
            if (result is Result.Success) {
                // immediately overwrite the "profile" LiveData with the new user
                _profile.postValue(Result.Success(result.data))
            }
        }
    }

    fun clearUpdateState() {
        _updateResult.value = null
    }
}

