package com.example.nomsy.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.nomsy.data.local.UserDatabase
import com.example.nomsy.data.local.models.User
import com.example.nomsy.data.remote.AuthApiService
import com.example.nomsy.data.remote.AuthRetrofitClient
import com.example.nomsy.data.remote.LoginRequest
import com.example.nomsy.data.remote.RegisterRequest
import com.example.nomsy.utils.Result
import kotlinx.coroutines.Dispatchers

class AuthRepository(
    private val authApi: AuthApiService = AuthRetrofitClient.authApi,
    private val userDatabase: UserDatabase
) : IUserRepository {

    override fun login(username: String, password: String): LiveData<Result<User>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val response = authApi.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        // Save user in local DB
                        userDatabase.userDao().insertUser(loginResponse.user)
                        emit(Result.Success(loginResponse.user))
                    } else {
                        emit(Result.Error(Exception("Login failed: no user returned")))
                    }
                } else {
                    emit(Result.Error(Exception("Login failed with status code ${response.code()}")))
                }
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }

    override fun register(user: User): LiveData<Result<User>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val request = RegisterRequest(
                    username = user.username,
                    password = user.password,
                    name = user.name,
                    age = user.age,
                    height = user.height,
                    weight = user.weight,
                    fitness_goal = user.fitness_goal,
                    nutrition_goals = user.nutrition_goals
                )
                val response = authApi.register(request)
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null) {
                        val registeredUser = user.copy(id = registerResponse.user_id)
                        // Save the registered user locally
                        userDatabase.userDao().insertUser(registeredUser)
                        emit(Result.Success(registeredUser))
                    } else {
                        emit(Result.Error(Exception("Registration failed: no response body")))
                    }
                } else {
                    emit(Result.Error(Exception("Registration failed: ${response.code()}")))
                }
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }

    override fun getProfile(userId: String): LiveData<Result<User>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val response = authApi.getProfile(userId)
                if (response.isSuccessful) {
                    val profileResponse = response.body()
                    if (profileResponse != null) {
                        // Optionally, update local DB with latest profile data
                        userDatabase.userDao().insertUser(profileResponse.user)
                        emit(Result.Success(profileResponse.user))
                    } else {
                        emit(Result.Error(Exception("Profile not found.")))
                    }
                } else {
                    emit(Result.Error(Exception("Profile fetch failed: ${response.code()}")))
                }
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }

    override fun getProfileByUsername(username: String): LiveData<Result<User>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                // Query Firestore for users with matching username
                val user = authApi.getUserByUsername(username) // You'll need to add this API method

                if (user.isSuccessful) {
                    val profileResponse = user.body()
                    if (profileResponse != null) {
                        // Optionally, update local DB with latest profile data
                        userDatabase.userDao().insertUser(profileResponse.user)
                        emit(Result.Success(profileResponse.user))
                    } else {
                        emit(Result.Error(Exception("Profile not found.")))
                    }
                } else {
                    emit(Result.Error(Exception("Profile fetch failed: ${user.code()}")))
                }
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }
}