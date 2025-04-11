package com.example.nomsy.viewModels
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.nomsy.data.local.UserDatabase
import com.example.nomsy.data.local.models.User
import com.example.nomsy.data.repository.AuthRepository
import com.example.nomsy.data.repository.IUserRepository
import com.example.nomsy.utils.Result
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userDatabase = UserDatabase.getDatabase(application)
    private val repository: IUserRepository = AuthRepository(userDatabase = userDatabase)

    var loginResult: LiveData<Result<User>>? = null
    var registerResult: LiveData<Result<User>>? = null
    var profileResult: LiveData<Result<User>>? = null

    fun login(username: String, password: String) {
        viewModelScope.launch {
            loginResult = repository.login(username, password)
            loginResult!!.observeForever { result ->
                Log.d("AuthViewModel", "Login result: $result")
            }
        }
    }

    fun register(user: User) {
        viewModelScope.launch {
            registerResult = repository.register(user)
        }
    }

    fun getProfile(userId: String) {
        viewModelScope.launch {
            profileResult = repository.getProfile(userId)
        }
    }
}