package com.example.nomsy.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.nomsy.data.local.UserDatabase
import com.example.nomsy.data.local.models.User
import com.example.nomsy.data.remote.GetProfileResponse
import com.example.nomsy.data.remote.LoginResponse
import com.example.nomsy.data.remote.RegisterResponse
import com.example.nomsy.data.remote.UpdateProfileRequest
import com.example.nomsy.testutil.FakeAuthApiService
import com.example.nomsy.testutil.FakeUserDatabase
import com.example.nomsy.testutil.getOrAwaitValue
import com.example.nomsy.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class AuthRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()


    private lateinit var fakeAuthApiService: FakeAuthApiService
    private lateinit var fakeUserDatabase: UserDatabase
    private lateinit var authRepository: AuthRepository


    private val testUser = User(
        id = "test-user-id",
        username = "testuser",
        password = "password123",
        name = "Test User",
        age = 25,
        height = 175,
        weight = 70,
        fitness_goal = "Maintain weight",
        nutrition_goals = mapOf("calories" to 2000, "protein" to 150)
    )

    @Before
    fun setup() {

        Dispatchers.setMain(testDispatcher)


        fakeAuthApiService = FakeAuthApiService()
        fakeUserDatabase = FakeUserDatabase.getInstance()

        authRepository = AuthRepository(
            authApi = fakeAuthApiService,
            userDatabase = fakeUserDatabase
        )


        fakeUserDatabase.clearAllTables()
        fakeAuthApiService.reset()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loginSuccessful() = runTest {

        val loginResponse = LoginResponse(
            message = "Login successful",
            user = testUser
        )
        fakeAuthApiService.loginResponse = Response.success(loginResponse)


        val resultLiveData = authRepository.login("testuser", "password123")
        val result = resultLiveData.getOrAwaitValue()


        assertTrue("Result should be Success but was $result", result is Result.Success)
        assertEquals(testUser, (result as Result.Success).data)

        val savedUser = fakeUserDatabase.userDao().getUserById(testUser.id)
        assertEquals(testUser, savedUser)
    }

    @Test
    fun loginError() = runTest {

        fakeAuthApiService.shouldThrowLoginException = true


        val resultLiveData = authRepository.login("testuser", "password123")
        val result = resultLiveData.getOrAwaitValue()


        assertTrue("Result should be Error but was $result", result is Result.Error)
        assertEquals("Login exception", (result as Result.Error).exception.message)
    }

    @Test
    fun registerSuccessful() = runTest {

        val registerResponse = RegisterResponse(
            message = "Registration successful",
            user_id = "new-user-id"
        )
        fakeAuthApiService.registerResponse = Response.success(registerResponse)

        val userToRegister = testUser.copy(id = "")


        val resultLiveData = authRepository.register(userToRegister)
        val result = resultLiveData.getOrAwaitValue()


        assertTrue("Result should be Success but was $result", result is Result.Success)
        assertEquals("new-user-id", (result as Result.Success).data.id)

        val savedUser = fakeUserDatabase.userDao().getUserById("new-user-id")
        assertEquals("new-user-id", savedUser?.id)
    }

    @Test
    fun registerError() = runTest {

        fakeAuthApiService.shouldThrowRegisterException = true


        val resultLiveData = authRepository.register(testUser)
        val result = resultLiveData.getOrAwaitValue()


        assertTrue("Result should be Error but was $result", result is Result.Error)
        assertEquals("Register exception", (result as Result.Error).exception.message)
    }

    @Test
    fun getProfileSuccessful() = runTest {

        val profileResponse = GetProfileResponse(user = testUser)
        fakeAuthApiService.getProfileResponse = Response.success(profileResponse)


        val resultLiveData = authRepository.getProfile(testUser.id)
        val result = resultLiveData.getOrAwaitValue()


        assertTrue("Result should be Success but was $result", result is Result.Success)
        assertEquals(testUser, (result as Result.Success).data)


        val savedUser = fakeUserDatabase.userDao().getUserById(testUser.id)
        assertEquals(testUser, savedUser)
    }

    @Test
    fun getProfileError() = runTest {

        fakeAuthApiService.shouldThrowGetProfileException = true


        val resultLiveData = authRepository.getProfile(testUser.id)
        val result = resultLiveData.getOrAwaitValue()


        assertTrue("Result should be Error but was $result", result is Result.Error)
        assertEquals("Get profile exception", (result as Result.Error).exception.message)
    }

    @Test
    fun getProfileByUsernameSuccessful() = runTest {

        val profileResponse = GetProfileResponse(user = testUser)
        fakeAuthApiService.getUserByUsernameResponse = Response.success(profileResponse)


        val resultLiveData = authRepository.getProfileByUsername(testUser.username)
        val result = resultLiveData.getOrAwaitValue()


        assertTrue("Result should be Success but was $result", result is Result.Success)
        assertEquals(testUser, (result as Result.Success).data)


        val savedUser = fakeUserDatabase.userDao().getUserByUsername(testUser.username)
        assertEquals(testUser, savedUser)
    }

    @Test
    fun getProfileByUsernameFallbackToLocal() = runTest {

        val userDao = fakeUserDatabase.userDao()
        userDao.insertUser(testUser)

        fakeAuthApiService.shouldThrowGetUserByUsernameException = true


        val resultLiveData = authRepository.getProfileByUsername(testUser.username)
        val result = resultLiveData.getOrAwaitValue()


        assertTrue("Result should be Success from local DB but was $result", result is Result.Success)
        assertEquals(testUser, (result as Result.Success).data)
    }

    @Test
    fun updateProfileSuccessful() = runTest {

        val updatedUser = testUser.copy(
            name = "Updated Name",
            weight = 75
        )
        val updateProfileResponse = GetProfileResponse(user = updatedUser)
        fakeAuthApiService.updateProfileResponse = Response.success(updateProfileResponse)

        val updateRequest = UpdateProfileRequest(
            name = "Updated Name",
            weight = 75
        )


        val resultLiveData = authRepository.updateProfile(testUser.username, updateRequest)
        val result = resultLiveData.getOrAwaitValue()


        assertTrue("Result should be Success but was $result", result is Result.Success)
        assertEquals("Updated Name", (result as Result.Success).data.name)
        assertEquals(75, result.data.weight)


        val savedUser = fakeUserDatabase.userDao().getUserByUsername(testUser.username)
        assertEquals("Updated Name", savedUser?.name)
        assertEquals(75, savedUser?.weight)
    }

    @Test
    fun updateProfileError() = runTest {

        fakeAuthApiService.shouldThrowUpdateProfileException = true
        val updateRequest = UpdateProfileRequest(name = "Updated Name")


        val resultLiveData = authRepository.updateProfile(testUser.username, updateRequest)
        val result = resultLiveData.getOrAwaitValue()


        assertTrue("Result should be Error but was $result", result is Result.Error)
        assertEquals("UpdateProfile exception", (result as Result.Error).exception.message)
    }
}