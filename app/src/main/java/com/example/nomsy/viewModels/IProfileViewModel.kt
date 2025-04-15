package com.example.nomsy.viewModels

import androidx.lifecycle.LiveData
import com.example.nomsy.data.local.models.User
import com.example.nomsy.data.remote.UpdateProfileRequest
import com.example.nomsy.utils.Result

interface IProfileViewModel {
    val profile: LiveData<Result<User>>
    val updateResult: LiveData<Result<User>?>
    fun fetchByUsername(username: String): kotlinx.coroutines.Job
    fun updateProfile(username: String, req: UpdateProfileRequest): kotlinx.coroutines.Job
    fun clearUpdateState()
}