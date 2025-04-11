package com.example.nomsy.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthRetrofitClient {
    // Use your Flask API base URL (e.g., your PythonAnywhere domain)
    private const val AUTHURL = "https://sonnguyen25.pythonanywhere.com/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(AUTHURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
    
}