package com.example.nomsy.food

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.nomsy.data.remote.AddMealRequest
import com.example.nomsy.data.remote.AddMealResponse
import com.example.nomsy.data.remote.AdjustWaterRequest
import com.example.nomsy.data.remote.DailySummaryResponse
import com.example.nomsy.data.remote.DeleteMealResponse
import com.example.nomsy.data.remote.FoodResponse
import com.example.nomsy.data.remote.MealTrackerApiService
import com.example.nomsy.data.remote.MealTrackerRetrofitClient
import com.example.nomsy.data.remote.WaterResponse
import com.example.nomsy.testutil.getOrAwaitValue
import com.example.nomsy.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response

import com.example.nomsy.data.local.entities.Food
import com.example.nomsy.data.remote.*
import com.example.nomsy.data.repository.FoodRepository
import org.junit.runners.JUnit4


class FakeMealTrackerApiService : MealTrackerApiService {

    var addMealResponse: Response<AddMealResponse>? = null
    var shouldThrowAddMealException = false

    override suspend fun addMeal(mealRequest: AddMealRequest): Response<AddMealResponse> {
        if (shouldThrowAddMealException) throw Exception("AddMeal exception")
        return addMealResponse
            ?: Response.error(
                500,
                "No addMeal response set".toResponseBody("application/json".toMediaTypeOrNull())
            )
    }

    var adjustWaterResponse: WaterResponse? = null
    var shouldThrowAdjustWaterException = false

    override suspend fun adjustWater(data: AdjustWaterRequest): WaterResponse {
        if (shouldThrowAdjustWaterException) throw Exception("AdjustWater exception")
        return adjustWaterResponse
            ?: WaterResponse(
                date = data.date,
                water = data.delta
            )
    }

    var dailySummaryResponse: Response<DailySummaryResponse>? = null
    var shouldThrowDailySummaryException = false

    override suspend fun getDailySummary(date: String): Response<DailySummaryResponse> {
        if (shouldThrowDailySummaryException) throw Exception("DailySummary exception")
        return dailySummaryResponse
            ?: Response.success(
                DailySummaryResponse(
                    date = date,
                    totals = NutritionTotals(
                        calories = 0,
                        carbs = 0,
                        protein = 0,
                        fat = 0,
                        water = 0.0
                    ),
                    meals = emptyMap()
                )
            )
    }

    var deleteMealResponse: Response<DeleteMealResponse>? = null
    var shouldThrowDeleteMealException = false

    override suspend fun deleteMeal(
        date: String,
        foodName: String
    ): Response<DeleteMealResponse> {
        if (shouldThrowDeleteMealException) throw Exception("DeleteMeal exception")
        return deleteMealResponse
            ?: Response.success(
                DeleteMealResponse(
                    message = "Deleted $foodName on $date",
                    success = true
                )
            )
    }

    var getAllFoodsResponse: Response<FoodResponse>? = null
    var shouldThrowGetAllFoodsException = false

    override suspend fun getAllFoods(): Response<FoodResponse> {
        if (shouldThrowGetAllFoodsException) throw Exception("GetAllFoods exception")
        return getAllFoodsResponse
            ?: Response.success(
                FoodResponse(
                    foods = listOf<Food>()
                )
            )
    }

    fun reset() {
        addMealResponse = null
        shouldThrowAddMealException = false

        adjustWaterResponse = null
        shouldThrowAdjustWaterException = false

        dailySummaryResponse = null
        shouldThrowDailySummaryException = false

        deleteMealResponse = null
        shouldThrowDeleteMealException = false

        getAllFoodsResponse = null
        shouldThrowGetAllFoodsException = false
    }
}







@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class FoodRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var fakeMealTrackerApiService: FakeMealTrackerApiService
    private lateinit var foodRepository: FoodRepository

    private val dummyMealRequest = AddMealRequest(
        date      = "2025-04-15",
        meal_type = "lunch",
        food_name = "Test Meal",
        calories  = 500,
        carbs     = 50,
        protein   = 25,
        fat       = 20
    )

    private val dummyMealResponse = AddMealResponse(
        message = "Meal added successfully",
        mealId  = "meal-id-123"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeMealTrackerApiService = FakeMealTrackerApiService()
        val delegateField = MealTrackerRetrofitClient::class.java
            .getDeclaredField("mealTrackerApi\$delegate")
            .apply { isAccessible = true }

        val lazyImpl = delegateField.get(null)!!

        val valueField = lazyImpl.javaClass.getDeclaredField("_value")
            .apply { isAccessible = true }

        valueField.set(lazyImpl, fakeMealTrackerApiService)


        foodRepository = FoodRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        fakeMealTrackerApiService.reset()
    }

    @Test
    fun addMealSuccessful() = runTest {
        fakeMealTrackerApiService.addMealResponse = Response.success(dummyMealResponse)

        val resultLiveData = foodRepository.addMeal(dummyMealRequest)
        val result = resultLiveData.getOrAwaitValue()

        assertTrue("Result should be Success but was $result", result is Result.Success)
        result as Result.Success
        assertEquals(dummyMealResponse, result.data)
    }

    @Test
    fun addMealError() = runTest {
        val errorMsg = "Invalid meal request"
        fakeMealTrackerApiService.addMealResponse = Response.error(
            400,
            errorMsg.toResponseBody("application/json".toMediaTypeOrNull())
        )

        val resultLiveData = foodRepository.addMeal(dummyMealRequest)
        val result = resultLiveData.getOrAwaitValue()

        assertTrue("Result should be Error but was $result", result is Result.Error)
        result as Result.Error
        assertTrue(result.exception.message?.contains("Failed to add meal:") == true)
        assertTrue(result.exception.message?.contains(errorMsg) == true)
    }

    @Test
    fun addMealException() = runTest {
        fakeMealTrackerApiService.shouldThrowAddMealException = true

        val resultLiveData = foodRepository.addMeal(dummyMealRequest)
        val result = resultLiveData.getOrAwaitValue()

        assertTrue("Result should be Error but was $result", result is Result.Error)
        result as Result.Error
        assertEquals("AddMeal exception", result.exception.message)
    }
}
