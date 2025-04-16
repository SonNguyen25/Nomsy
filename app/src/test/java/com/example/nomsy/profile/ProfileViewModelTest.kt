package com.example.nomsy.profile

import android.app.Application
import android.os.Looper
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
import com.example.nomsy.viewModels.ProfileViewModel
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
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import retrofit2.Response
import java.lang.reflect.Field

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

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

        // inject our fake repo
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
        // arrange
        fakeAuthApiService.getUserByUsernameResponse = Response.success(GetProfileResponse(user = testUser))

        // act
        profileViewModel.fetchByUsername(testUser.username)
        testDispatcher.scheduler.advanceUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()   // <— ensure LiveData posts

        // assert
        val result = profileViewModel.profile.getOrAwaitValue()
        assertTrue("Expected Result.Success but got $result", result is Result.Success)
        assertEquals(testUser, (result as Result.Success).data)
    }

    @Test
    fun fetchByUsernameError() = runTest {
        // arrange: make the API throw
        fakeAuthApiService.shouldThrowGetUserByUsernameException = true

        // act
        profileViewModel.fetchByUsername(testUser.username)
        testDispatcher.scheduler.advanceUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()

        // assert
        val result = profileViewModel.profile.getOrAwaitValue()
        assertTrue("Expected Result.Error but got $result", result is Result.Error)
        assertEquals("GetUserByUsername exception", (result as Result.Error).exception.message)
    }

    @Test
    fun updateProfileSuccessful() = runTest {
        // arrange
        val updated = testUser.copy(name = "Updated", weight = 75)
        fakeAuthApiService.updateProfileResponse = Response.success(GetProfileResponse(user = updated))
        val req = UpdateProfileRequest(name = "Updated", weight = 75)

        // act
        profileViewModel.updateProfile(testUser.username, req)
        testDispatcher.scheduler.advanceUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()

        // assert updateResult
        val upd = profileViewModel.updateResult.getOrAwaitValue()
        assertTrue(upd is Result.Success)
        assertEquals("Updated", (upd as Result.Success).data.name)
        assertEquals(75, upd.data.weight)

        // assert profile was also updated
        val prof = profileViewModel.profile.getOrAwaitValue()
        assertTrue(prof is Result.Success)
        assertEquals(updated, (prof as Result.Success).data)
    }

    @Test
    fun updateProfileError() = runTest {
        // arrange
        fakeAuthApiService.shouldThrowUpdateProfileException = true
        val req = UpdateProfileRequest(name = "Bad")

        // act
        profileViewModel.updateProfile(testUser.username, req)
        testDispatcher.scheduler.advanceUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()

        // assert
        val upd = profileViewModel.updateResult.getOrAwaitValue()
        assertTrue("Expected Result.Error but got $upd", upd is Result.Error)
        assertEquals("UpdateProfile exception", (upd as Result.Error).exception.message)
    }

    @Test
    fun clearUpdateStateClearsUpdateResult() = runTest {
        // pre‑populate
        profileViewModel.updateResult.postValue(Result.Success(testUser))
        assertNotNull("updateResult should be set", profileViewModel.updateResult.value)

        // act
        profileViewModel.clearUpdateState()
        testDispatcher.scheduler.advanceUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()

        // assert
        assertNull("updateResult should be null after clearing", profileViewModel.updateResult.value)
    }
}
