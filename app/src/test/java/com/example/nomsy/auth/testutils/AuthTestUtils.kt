package com.example.nomsy.testutil

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.nomsy.data.local.UserDatabase
import com.example.nomsy.data.local.dao.UserDao
import com.example.nomsy.data.local.models.User
import com.example.nomsy.utils.Result
import com.example.nomsy.data.remote.*
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun <T> LiveData<T>.getOrAwaitValue(
    skipLoading: Boolean = true,
    time: Long = 10,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            if (skipLoading && value is Result.Loading) {
                return
            }
            data = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)
    afterObserve.invoke()
    if (!latch.await(time, timeUnit)) {
        this.removeObserver(observer)
        throw TimeoutException("LiveData value was never set.")
    }
    @Suppress("UNCHECKED_CAST")
    return data as T
}

class TimeoutException(message: String) : Exception(message)

class FakeAuthApiService : AuthApiService {
    var loginResponse: Response<LoginResponse>? = null
    var shouldThrowLoginException = false

    override suspend fun login(loginRequest: LoginRequest): Response<LoginResponse> {
        if (shouldThrowLoginException) throw Exception("Login exception")
        return loginResponse ?: Response.error(500, "No login response set".toResponseBody("application/json".toMediaTypeOrNull()))
    }

    var registerResponse: Response<RegisterResponse>? = null
    var shouldThrowRegisterException = false

    override suspend fun register(registerRequest: RegisterRequest): Response<RegisterResponse> {
        if (shouldThrowRegisterException) throw Exception("Register exception")
        return registerResponse ?: Response.error(500, "No register response set".toResponseBody("application/json".toMediaTypeOrNull()))
    }

    var getProfileResponse: Response<GetProfileResponse>? = null
    var shouldThrowGetProfileException = false

    override suspend fun getProfile(userId: String): Response<GetProfileResponse> {
        if (shouldThrowGetProfileException) throw Exception("Get profile exception")
        return getProfileResponse ?: Response.error(500, "No getProfile response set".toResponseBody("application/json".toMediaTypeOrNull()))
    }

    var getUserByUsernameResponse: Response<GetProfileResponse>? = null
    var shouldThrowGetUserByUsernameException = false

    override suspend fun getUserByUsername(username: String): Response<GetProfileResponse> {
        if (shouldThrowGetUserByUsernameException) throw Exception("GetUserByUsername exception")
        return getUserByUsernameResponse ?: Response.error(500, "No getUserByUsername response set".toResponseBody("application/json".toMediaTypeOrNull()))
    }

    var updateProfileResponse: Response<GetProfileResponse>? = null
    var shouldThrowUpdateProfileException = false

    override suspend fun updateProfile(
        username: String,
        request: UpdateProfileRequest
    ): Response<GetProfileResponse> {
        if (shouldThrowUpdateProfileException) throw Exception("UpdateProfile exception")
        return updateProfileResponse ?: Response.error(500, "No updateProfile response set".toResponseBody("application/json".toMediaTypeOrNull()))
    }

    fun reset() {
        loginResponse = null
        shouldThrowLoginException = false
        registerResponse = null
        shouldThrowRegisterException = false
        getProfileResponse = null
        shouldThrowGetProfileException = false
        getUserByUsernameResponse = null
        shouldThrowGetUserByUsernameException = false
        updateProfileResponse = null
        shouldThrowUpdateProfileException = false
    }
}

class FakeUserDao : UserDao {
    private var usersById: MutableMap<String, User> = mutableMapOf()
    private var usersByUsername: MutableMap<String, User> = mutableMapOf()

    override suspend fun insertUser(user: User) {
        if (user.id.isNotEmpty()) {
            usersById[user.id] = user
        }
        usersByUsername[user.username] = user
    }

    override suspend fun getUserByUsername(username: String): User? =
        usersByUsername[username]

    override suspend fun getUserById(userId: String): User? =
        usersById[userId]

    suspend fun clearAllUsers() {
        usersById.clear()
        usersByUsername.clear()
    }
}

class FakeUserDatabase private constructor() : UserDatabase() {
    private val fakeUserDao = FakeUserDao()
    override fun userDao(): UserDao = fakeUserDao
    override fun clearAllTables() {
        runBlocking { (userDao() as FakeUserDao).clearAllUsers() }
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        return InvalidationTracker(this)
    }

    override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
        throw UnsupportedOperationException("Fake database doesn't support this operation")
    }

    companion object {
        @Volatile private var instance: FakeUserDatabase? = null
        fun getInstance(): FakeUserDatabase =
            instance ?: synchronized(this) { instance ?: FakeUserDatabase().also { instance = it } }
        fun getDatabase(context: android.content.Context): UserDatabase = getInstance()
    }
}