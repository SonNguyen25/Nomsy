package com.example.nomsy.data.remote

import com.example.nomsy.data.local.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val message: String, val user: User)

data class RegisterRequest(
    val username: String,
    val password: String,
    val name: String,
    val age: Int,
    val height: Double,
    val weight: Double,
    val fitness_goal: String,
    val nutrition_goals: Map<String, Int>
)
data class RegisterResponse(val message: String, val user_id: String)

// Response for getProfile endpoint.
data class GetProfileResponse(val user: User)

interface AuthApiService {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @GET("profile/{user_id}")
    suspend fun getProfile(@Path("user_id") userId: String): Response<GetProfileResponse>
}