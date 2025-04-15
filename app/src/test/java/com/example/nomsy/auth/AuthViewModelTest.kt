package com.example.nomsy.viewModels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.nomsy.data.local.models.User
import com.example.nomsy.data.remote.GetProfileResponse
import com.example.nomsy.data.remote.LoginResponse
import com.example.nomsy.data.remote.RegisterResponse
import com.example.nomsy.data.remote.UpdateProfileRequest
import com.example.nomsy.data.repository.AuthRepository
import com.example.nomsy.data.repository.IUserRepository
import com.example.nomsy.testutil.FakeAuthApiService
import com.example.nomsy.testutil.FakeUserDatabase
import com.example.nomsy.testutil.getOrAwaitValue
import com.example.nomsy.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response
import java.lang.reflect.Field

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeAuthApiService: FakeAuthApiService
    private lateinit var fakeUserDatabase: FakeUserDatabase
    private lateinit var authViewModel: AuthViewModel

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

        authViewModel = AuthViewModel(Application())

        val field: Field = AuthViewModel::class.java.getDeclaredField("repository")
        field.isAccessible = true
        val testRepository: IUserRepository =
            AuthRepository(authApi = fakeAuthApiService, userDatabase = fakeUserDatabase)
        field.set(authViewModel, testRepository)

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

        authViewModel.login("testuser", "password123")
        testDispatcher.scheduler.advanceUntilIdle()

        val result = authViewModel.loginResult.getOrAwaitValue()
        assertTrue("Result should be Success but was $result", result is Result.Success)
        assertEquals(testUser, (result as Result.Success).data)
        assertTrue(authViewModel.isLoggedIn.value)
        assertEquals("testuser", authViewModel.getCurrentUsername())
    }

    @Test
    fun loginError() = runTest {
        fakeAuthApiService.shouldThrowLoginException = true

        authViewModel.login("testuser", "password123")
        testDispatcher.scheduler.advanceUntilIdle()

        val result = authViewModel.loginResult.getOrAwaitValue()
        assertTrue("Result should be Error but was $result", result is Result.Error)
        assertEquals("Login exception", (result as Result.Error).exception.message)
        assertFalse(authViewModel.isLoggedIn.value)
    }

    @Test
    fun registerSuccessful() = runTest {
        val registerResponse = RegisterResponse(
            message = "Registration successful",
            user_id = "new-user-id"
        )
        fakeAuthApiService.registerResponse = Response.success(registerResponse)
        val userToRegister = testUser.copy(id = "")

        authViewModel.register(userToRegister)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = authViewModel.registerResult.getOrAwaitValue()
        assertTrue("Result should be Success but was $result", result is Result.Success)
        assertEquals("new-user-id", (result as Result.Success).data.id)
        assertTrue(authViewModel.isLoggedIn.value)
    }

    @Test
    fun registerError() = runTest {
        fakeAuthApiService.shouldThrowRegisterException = true

        authViewModel.register(testUser)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = authViewModel.registerResult.getOrAwaitValue()
        assertTrue("Result should be Error but was $result", result is Result.Error)
        assertEquals("Register exception", (result as Result.Error).exception.message)
    }

    @Test
    fun fetchProfileSuccessful() = runTest {
        val profileResponse = GetProfileResponse(user = testUser)
        fakeAuthApiService.getProfileResponse = Response.success(profileResponse)

        authViewModel.fetchProfile(testUser.id)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = authViewModel.profileResult.getOrAwaitValue()
        assertTrue("Result should be Success but was $result", result is Result.Success)
        assertEquals(testUser, (result as Result.Success).data)
    }

    @Test
    fun fetchProfileError() = runTest {
        fakeAuthApiService.shouldThrowGetProfileException = true

        authViewModel.fetchProfile(testUser.id)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = authViewModel.profileResult.getOrAwaitValue()
        assertTrue("Result should be Error but was $result", result is Result.Error)
        assertEquals("Get profile exception", (result as Result.Error).exception.message)
    }

    @Test
    fun fetchProfileByUsernameSuccessful() = runTest {
        val profileResponse = GetProfileResponse(user = testUser)
        fakeAuthApiService.getUserByUsernameResponse = Response.success(profileResponse)

        authViewModel.fetchProfileByUsername(testUser.username)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = authViewModel.profileResult.getOrAwaitValue()
        assertTrue("Result should be Success but was $result", result is Result.Success)
        assertEquals(testUser, (result as Result.Success).data)
    }

    @Test
    fun logoutClearsState() = runTest {
        val loginResponse = LoginResponse(
            message = "Login successful",
            user = testUser
        )
        fakeAuthApiService.loginResponse = Response.success(loginResponse)
        authViewModel.login("testuser", "password123")
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(authViewModel.isLoggedIn.value)
        assertNotNull(authViewModel.loginResult.value)
        assertEquals("testuser", authViewModel.getCurrentUsername())

        authViewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(authViewModel.loginResult.value)
        assertFalse(authViewModel.isLoggedIn.value)
        assertEquals("", authViewModel.getCurrentUsername())
    }
}
