package com.example.nomsy.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response
import java.lang.reflect.Field

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var fakeAuthApiService: FakeAuthApiService
    private lateinit var fakeUserDatabase: FakeUserDatabase
    private lateinit var profileViewModel: ProfileViewModel

    private val testApplication = TestApplication()

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

        val testRepository = AuthRepository(
            userDatabase = fakeUserDatabase,
            authApi = fakeAuthApiService
        )

        val testApp = object : TestApplication() {
            override fun getApplicationContext(): android.content.Context {
                return this
            }
        }

        profileViewModel = ProfileViewModel(testApp)

        val repoField: Field = ProfileViewModel::class.java.getDeclaredField("repo")
        repoField.isAccessible = true
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
        fakeAuthApiService.getUserByUsernameResponse = Response.success(GetProfileResponse(user = testUser))

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
        val updated = testUser.copy(name = "Updated", weight = 75)
        fakeAuthApiService.updateProfileResponse = Response.success(GetProfileResponse(user = updated))
        val req = UpdateProfileRequest(name = "Updated", weight = 75)

        profileViewModel.updateProfile(testUser.username, req)
        testDispatcher.scheduler.advanceUntilIdle()

        val upd = profileViewModel.updateResult.getOrAwaitValue()
        assertTrue(upd is Result.Success)
        assertEquals("Updated", (upd as Result.Success).data.name)
        assertEquals(75, upd.data.weight)

        val prof = profileViewModel.profile.getOrAwaitValue()
        assertTrue(prof is Result.Success)
        assertEquals(updated, (prof as Result.Success).data)
    }

    @Test
    fun updateProfileError() = runTest {
        fakeAuthApiService.shouldThrowUpdateProfileException = true
        val req = UpdateProfileRequest(name = "Bad")

        profileViewModel.updateProfile(testUser.username, req)
        testDispatcher.scheduler.advanceUntilIdle()

        val upd = profileViewModel.updateResult.getOrAwaitValue()
        assertTrue("Expected Result.Error but got $upd", upd is Result.Error)
        assertEquals("UpdateProfile exception", (upd as Result.Error).exception.message)
    }

    @Test
    fun clearUpdateStateClearsUpdateResult() = runTest {
        profileViewModel.updateResult.postValue(Result.Success(testUser))
        assertNotNull("updateResult should be set", profileViewModel.updateResult.value)

        profileViewModel.clearUpdateState()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull("updateResult should be null after clearing", profileViewModel.updateResult.value)
    }

    open class TestApplication : android.app.Application() {
        override fun getApplicationContext(): android.content.Context {
            return this
        }
    }
}