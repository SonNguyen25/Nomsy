package com.example.nomsy.viewModels
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    // --- Login ---
    private val _loginResult = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>> = _loginResult

    fun login(username: String, password: String) {
        // Kick off the LiveData from the repo and mirror its emissions
        repository.login(username, password)
            .observeForever { result ->
                _loginResult.postValue(result)
            }
    }

    // --- Register ---
    private val _registerResult = MutableLiveData<Result<User>>()
    val registerResult: LiveData<Result<User>> = _registerResult

    fun register(user: User) {
        user.fitness_goal.lowercase()
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

    private var tempUsername: String? = null
    private var tempPassword: String? = null
    private var tempEmail: String? = null
    private var tempName: String? = null
    private var tempAge: Int? = null
    private var tempHeight: Double? = null
    private var tempWeight: Double? = null
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

    fun setUserHeight(height: Double) {
        this.tempHeight = height
    }

    fun setUserWeight(weight: Double) {
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
    fun getUserHeight(): Double = this.tempHeight ?: 0.0
    fun getUserWeight(): Double = this.tempWeight ?: 0.0
    fun getUserFitnessGoal(): String = this.tempFitnessGoal ?: ""


}