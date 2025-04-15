package com.example.nomsy.viewModels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.example.nomsy.data.local.entities.User
import com.example.nomsy.data.remote.GetProfileResponse
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
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response
import java.lang.reflect.Field

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeAuthApiService: FakeAuthApiService
    private lateinit var fakeUserDatabase: FakeUserDatabase

    private lateinit var profileViewModel: ProfileViewModel


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

        val appContext = ApplicationProvider.getApplicationContext<Application>()

        fakeAuthApiService = FakeAuthApiService()
        fakeUserDatabase = FakeUserDatabase.getInstance()

        profileViewModel = ProfileViewModel(appContext)

        val repoField: Field = ProfileViewModel::class.java.getDeclaredField("repo")
        repoField.isAccessible = true
        val testRepository: IUserRepository =
            AuthRepository(userDatabase = fakeUserDatabase, authApi = fakeAuthApiService)
        repoField.set(profileViewModel, testRepository)

        fakeUserDatabase.clearAllTables()
        fakeAuthApiService.reset()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchByUsernameSuccessful() = runTest {
        val profileResponse = GetProfileResponse(user = testUser)
        fakeAuthApiService.getUserByUsernameResponse = Response.success(profileResponse)

        profileViewModel.fetchByUsername(testUser.username)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = profileViewModel.profile.getOrAwaitValue()
        assertTrue("Expected Result.Success but got $result", result is Result.Success)
        assertEquals(testUser, (result as Result.Success).data)
    }

    @Test
    fun fetchByUsernameError() = runTest {
        fakeAuthApiService.shouldThrowGetUserByUsernameException = true

        profileViewModel.fetchByUsername(testUser.username)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = profileViewModel.profile.getOrAwaitValue()
        assertTrue("Expected Result.Error but got $result", result is Result.Error)
        assertEquals("GetUserByUsername exception", (result as Result.Error).exception.message)
    }

    @Test
    fun updateProfileSuccessful() = runTest {
        val updatedUser = testUser.copy(name = "Updated Name", weight = 75)
        val updateProfileResponse = GetProfileResponse(user = updatedUser)
        fakeAuthApiService.updateProfileResponse = Response.success(updateProfileResponse)

        val updateRequest = UpdateProfileRequest(name = "Updated Name", weight = 75)

        profileViewModel.updateProfile(testUser.username, updateRequest)
        testDispatcher.scheduler.advanceUntilIdle()

        val updateResult = profileViewModel.updateResult.getOrAwaitValue()
        assertNotNull("updateResult should not be null", updateResult)
        assertTrue("Expected Result.Success but got $updateResult", updateResult is Result.Success)
        assertEquals("Updated Name", (updateResult as Result.Success).data.name)
        assertEquals(75, updateResult.data.weight)

        val profileResult = profileViewModel.profile.getOrAwaitValue()
        assertTrue("Expected profile Result.Success but got $profileResult", profileResult is Result.Success)
        assertEquals(updatedUser, (profileResult as Result.Success).data)
    }

    @Test
    fun updateProfileError() = runTest {
        fakeAuthApiService.shouldThrowUpdateProfileException = true
        val updateRequest = UpdateProfileRequest(name = "Updated Name")

        profileViewModel.updateProfile(testUser.username, updateRequest)
        testDispatcher.scheduler.advanceUntilIdle()

        val updateResult = profileViewModel.updateResult.getOrAwaitValue()
        assertTrue("Expected Result.Error but got $updateResult", updateResult is Result.Error)
        assertEquals("UpdateProfile exception", (updateResult as Result.Error).exception.message)
    }

    @Test
    fun clearUpdateStateClearsUpdateResult() = runTest {
        profileViewModel.updateResult.postValue(Result.Success(testUser))
        assertNotNull("updateResult should be set", profileViewModel.updateResult.value)

        profileViewModel.clearUpdateState()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull("updateResult should be null after clearing", profileViewModel.updateResult.value)
    }
}
