package com.example.nomsy.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nomsy.data.local.UserDatabase
import com.example.nomsy.data.local.models.User
import com.example.nomsy.data.repository.AuthRepository
import com.example.nomsy.data.repository.IUserRepository
import com.example.nomsy.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userDatabase = UserDatabase.getDatabase(application)
    private val repository: IUserRepository = AuthRepository(userDatabase = userDatabase)

    // --- Login ---
    private val _loginResult = MutableLiveData<Result<User>?>()
    val loginResult: LiveData<Result<User>?> = _loginResult

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Store the current username
    private var currentUsername: String = ""

    // Methods to get/set current username
    fun setCurrentUsername(username: String) {
        currentUsername = username
//        _isLoggedIn.value = username.isNotEmpty()
    }

    fun getCurrentUsername(): String {
        return currentUsername
    }

    fun login(username: String, password: String) {
        repository.login(username, password)
            .observeForever { result ->
                _loginResult.postValue(result)
                if (result is Result.Success) {
                    setCurrentUsername(username)
                    _isLoggedIn.value = true
                }
            }
    }

    fun logout() {
        setCurrentUsername("")
        _isLoggedIn.value = false
        _loginResult.value = null


    }

    // --- Register ---
    private val _registerResult = MutableLiveData<Result<User>>()
    val registerResult: LiveData<Result<User>> = _registerResult

    fun register(user: User) {
        tempUsername?.let { setCurrentUsername(it) }
        repository.register(user)
            .observeForever { result ->
                _registerResult.postValue(result)
            }
    }

    // --- Profile fetch ---
    private val _profileResult = MutableLiveData<Result<User>>()
    val profileResult: LiveData<Result<User>> = _profileResult

    fun fetchProfile(userId: String) {
        repository.getProfile(userId)
            .observeForever { result ->
                _profileResult.postValue(result)
            }
    }

    fun fetchProfileByUsername(username: String = currentUsername) {
        repository.getProfileByUsername(username)
            .observeForever { result ->
                _profileResult.postValue(result)
            }
    }

    private var tempUsername: String? = null
    private var tempPassword: String? = null
    private var tempEmail: String? = null
    private var tempName: String? = null
    private var tempAge: Int? = null
    private var tempHeight: Int? = null
    private var tempWeight: Int? = null
    private var tempFitnessGoal: String? = null
    private var tempNutritionGoals: Map<String, Int>? = null


    fun setCredentials(username: String, password: String, email: String) {
        this.tempUsername = username
        this.tempPassword = password
        this.tempEmail = email
    }

    fun setUserName(name: String) {
        this.tempName = name
    }

    fun setUserAge(age: Int) {
        this.tempAge = age
    }

    fun setUserHeight(height: Int) {
        this.tempHeight = height
    }

    fun setUserWeight(weight: Int) {
        this.tempWeight = weight
    }

    fun setUserFitnessGoal(goal: String) {
        this.tempFitnessGoal = goal
    }

    fun setUserNutritionGoals(goals: Map<String, Int>) {
        this.tempNutritionGoals = goals
    }

    fun getUsername(): String = this.tempUsername ?: ""
    fun getPassword(): String = this.tempPassword ?: ""
    fun getUserName(): String = this.tempName ?: ""
    fun getUserAge(): Int = this.tempAge ?: 0
    fun getUserHeight(): Int = this.tempHeight ?: 0
    fun getUserWeight(): Int = this.tempWeight ?: 0
    fun getUserFitnessGoal(): String = this.tempFitnessGoal ?: ""


}