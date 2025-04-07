package com.example.nomsy.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nomsy.models.User
import com.example.nomsy.models.IUserRepository

class AuthViewModel(private val userRepository: IUserRepository) : ViewModel() {

    private val _user = MutableLiveData<Result<User>>()
    val user: LiveData<Result<User>> = _user

    fun login(username: String, password: String) {
        _user.value = Result.loading()
        userRepository.login(username, password).observeForever {
//            _user.value = it
        }
    }

    fun register(user: User) {
        _user.value = Result.loading()
        userRepository.register(user).observeForever {
//            _user.value = it
        }
    }

    fun loadProfile(userId: String) {
        _user.value = Result.loading()
        userRepository.getProfile(userId).observeForever {
//            _user.value = it
        }
    }
}