package com.example.nomsy.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.nomsy.data.local.UserDatabase
import com.example.nomsy.data.local.models.FoodLog

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class
HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val userDatabase = UserDatabase.getDatabase(application)
//    private val repository = FoodRepository(userDatabase)
    private val authViewModel = AuthViewModel(application)

    private val _selectedDate = MutableStateFlow(getTodayTimestamp())
    private val _foodLogs = MutableStateFlow<List<FoodLog>>(emptyList())

    init {
        loadHomeScreenData()
    }

    fun loadHomeScreenData() {
        viewModelScope.launch {

        }
    }

    fun changeDate(date: Long) {

    }

    fun incrementDate() {
//
    }

    fun decrementDate() {
//
    }

    fun updateWaterIntake(amount: Float) {
        viewModelScope.launch {
            try {

                // Data will refresh through flows
            } catch (e: Exception) {

            }
        }
    }

    fun deleteFoodLog(id: String) {
        viewModelScope.launch {
            try {

                // Data will refresh through flows
            } catch (e: Exception) {

            }
        }
    }

    private fun getTodayTimestamp() {

    }
}