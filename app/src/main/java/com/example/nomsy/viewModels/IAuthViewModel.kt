package com.example.nomsy.viewModels

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.StateFlow
import com.example.nomsy.data.local.entities.User
import com.example.nomsy.utils.Result

interface IAuthViewModel {
    // Observable properties
    val loginResult: LiveData<Result<User>?>
    val isLoggedIn: StateFlow<Boolean>
    val registerResult: LiveData<Result<User>>
    val profileResult: LiveData<Result<User>>

    // Methods for managing the current user
    fun setCurrentUsername(username: String)
    fun getCurrentUsername(): String

    // Authentication methods
    fun login(username: String, password: String)
    fun logout()

    // Registration methods
    fun register(user: User)

    // Profile retrieval methods
    fun fetchProfile(userId: String)
    fun fetchProfileByUsername(username: String = getCurrentUsername())

    // Methods to set user credentials and profile details
    fun setCredentials(username: String, password: String, email: String)
    fun setUserName(name: String)
    fun setUserAge(age: Int)
    fun setUserHeight(height: Int)
    fun setUserWeight(weight: Int)
    fun setUserFitnessGoal(goal: String)
    fun setUserNutritionGoals(goals: Map<String, Int>)

    // Getter methods for user information
    fun getUsername(): String
    fun getPassword(): String
    fun getUserName(): String
    fun getUserAge(): Int
    fun getUserHeight(): Int
    fun getUserWeight(): Int
    fun getUserFitnessGoal(): String
}
