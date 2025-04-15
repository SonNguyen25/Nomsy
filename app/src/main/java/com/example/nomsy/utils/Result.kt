package com.example.nomsy.utils

sealed class Result<out T> {
    object Loading : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    companion object {
        fun <T> loading(): Result<T> = Loading as Result<T>
        fun <T> error(e: Exception): Result<T> = Error(e) as Result<T>
    }

}