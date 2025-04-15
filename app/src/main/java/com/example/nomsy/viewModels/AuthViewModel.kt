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

class AuthViewModel(application: Application) : AndroidViewModel(application), IAuthViewModel {
    private val userDatabase = UserDatabase.getDatabase(application)
    private val repository: IUserRepository = AuthRepository(userDatabase = userDatabase)

    // --- Login ---
    private val _loginResult = MutableLiveData<Result<User>?>()
    override val loginResult: LiveData<Result<User>?> = _loginResult

    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Store the current username
    private var currentUsername: String = ""

    // Methods to get/set current username
    override fun setCurrentUsername(username: String) {
        currentUsername = username
//        _isLoggedIn.value = username.isNotEmpty()
    }

    override fun getCurrentUsername(): String {
        return currentUsername
    }

    override fun login(username: String, password: String) {
        repository.login(username, password)
            .observeForever { result ->
                _loginResult.postValue(result)
                if (result is Result.Success) {
                    setCurrentUsername(username)
                    _isLoggedIn.value = true
                }
            }
    }

    override fun logout() {
        setCurrentUsername("")
        _isLoggedIn.value = false
        _loginResult.value = null


    }

    // --- Register ---
    private val _registerResult = MutableLiveData<Result<User>>()
    override val registerResult: LiveData<Result<User>> = _registerResult

    override fun register(user: User) {
        tempUsername?.let { setCurrentUsername(it) }
        repository.register(user)
            .observeForever { result ->
                _registerResult.postValue(result)
                _isLoggedIn.value = true
            }
    }

    // --- Profile fetch ---
    private val _profileResult = MutableLiveData<Result<User>>()
    override val profileResult: LiveData<Result<User>> = _profileResult

    override fun fetchProfile(userId: String) {
        repository.getProfile(userId)
            .observeForever { result ->
                _profileResult.postValue(result)
            }
    }

    override fun fetchProfileByUsername(username: String) {
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


    override fun setCredentials(username: String, password: String, email: String) {
        this.tempUsername = username
        this.tempPassword = password
        this.tempEmail = email
    }

    override fun setUserName(name: String) {
        this.tempName = name
    }

    override fun setUserAge(age: Int) {
        this.tempAge = age
    }

    override fun setUserHeight(height: Int) {
        this.tempHeight = height
    }

    override fun setUserWeight(weight: Int) {
        this.tempWeight = weight
    }

    override fun setUserFitnessGoal(goal: String) {
        this.tempFitnessGoal = goal
    }

    override fun setUserNutritionGoals(goals: Map<String, Int>) {
        this.tempNutritionGoals = goals
    }

    override fun getUsername(): String = this.tempUsername ?: ""
    override fun getPassword(): String = this.tempPassword ?: ""
    override fun getUserName(): String = this.tempName ?: ""
    override fun getUserAge(): Int = this.tempAge ?: 0
    override fun getUserHeight(): Int = this.tempHeight ?: 0
    override fun getUserWeight(): Int = this.tempWeight ?: 0
    override fun getUserFitnessGoal(): String = this.tempFitnessGoal ?: ""


}