package com.example.nomsy.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.nomsy.data.local.dao.MealTrackerDao
import com.example.nomsy.data.local.entities.DailySummaryEntity
import com.example.nomsy.data.local.entities.MealEntity
import com.example.nomsy.data.remote.AddMealRequest
import com.example.nomsy.data.remote.AddMealResponse
import com.example.nomsy.data.remote.AdjustWaterRequest
import com.example.nomsy.data.remote.DailySummaryResponse
import com.example.nomsy.data.remote.DeleteMealResponse
import com.example.nomsy.data.remote.FoodResponse
import com.example.nomsy.data.remote.MealItem
import com.example.nomsy.data.remote.MealTrackerApiService
import com.example.nomsy.data.remote.NutritionTotals
import com.example.nomsy.data.remote.WaterResponse
import com.example.nomsy.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class MealTrackerRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeApiService: FakeMealTrackerApiService
    private lateinit var fakeDao: FakeMealTrackerDao
    private lateinit var mealTrackerRepository: MealTrackerRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeApiService = FakeMealTrackerApiService()
        fakeDao = FakeMealTrackerDao()

        mealTrackerRepository = MealTrackerRepository(
            mealApiService = fakeApiService,
            mealDao = fakeDao
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getDailyNutritionTotalsSuccess() = runTest {
        val testDate = "2025-04-14"
        val mockSummaryResponse = DailySummaryResponse(
            date = testDate,
            totals = NutritionTotals(
                calories = 1500,
                carbs = 150,
                protein = 90,
                fat = 50,
                water = 2.0
            ),
            meals = emptyMap()
        )
        fakeApiService.setDailySummaryResponse(testDate, Response.success(mockSummaryResponse))
        val result = mealTrackerRepository.getDailyNutritionTotals(testDate)
        assertTrue(result is Result.Success)
        assertTrue(fakeDao.insertDailySummaryCalled)
    }

    @Test
    fun getDailyNutritionTotalsApiFailure() = runTest {
        val testDate = "2025-04-14"
        val mockSummary = DailySummaryEntity(
            date = testDate,
            totalCalories = 1500,
            totalCarbs = 150,
            totalProtein = 90,
            totalFat = 50,
            waterLiters = 2.0
        )
        fakeApiService.setDailySummaryResponse(
            testDate,
            Response.error(500, okhttp3.ResponseBody.create(null, ""))
        )
        fakeDao.setDailySummary(testDate, mockSummary)

        val result = mealTrackerRepository.getDailyNutritionTotals(testDate)
        assertTrue(result is Result.Success)
        assertFalse(fakeDao.insertDailySummaryCalled)
    }

    @Test
    fun getDailyNutritionTotalsNetworkError() = runTest {
        val testDate = "2025-04-14"
        val mockSummary = DailySummaryEntity(
            date = testDate,
            totalCalories = 1500,
            totalCarbs = 150,
            totalProtein = 90,
            totalFat = 50,
            waterLiters = 2.0
        )
        fakeApiService.setShouldThrowNetworkError(true)
        fakeDao.setDailySummary(testDate, mockSummary)
        val result = mealTrackerRepository.getDailyNutritionTotals(testDate)
        assertTrue(result is Result.Success)
        assertFalse(fakeDao.insertDailySummaryCalled)
    }

    @Test
    fun getMealsByDateSuccess() = runTest {
        val testDate = "2025-04-14"
        val mealItems = listOf(
            MealItem("Eggs", 150, 2, 12, 10),
            MealItem("Toast", 90, 15, 3, 2)
        )
        val mealMap = mapOf("breakfast" to mealItems)

        val mockSummaryResponse = DailySummaryResponse(
            date = testDate,
            totals = NutritionTotals(
                calories = 240,
                carbs = 17,
                protein = 15,
                fat = 12,
                water = 0.0
            ),
            meals = mealMap
        )

        fakeApiService.setDailySummaryResponse(testDate, Response.success(mockSummaryResponse))

        val result = mealTrackerRepository.getMealsByDate(testDate)

        assertTrue(result is Result.Success)
        val mealsData = (result as Result.Success).data
        assertEquals(1, mealsData.size)
        assertEquals(2, mealsData["breakfast"]?.size)
        assertTrue(fakeDao.insertMealsCalled)
        assertTrue(fakeDao.deleteMealsByDateCalled)
    }

    @Test
    fun getMealsByDateApiFailure() = runTest {
        val testDate = "2025-04-14"
        val mockEntities = listOf(
            MealEntity(
                id = 1,
                mealId = "123",
                date = testDate,
                mealType = "breakfast",
                food_name = "Eggs",
                calories = 150,
                carbs = 2,
                protein = 12,
                fat = 10
            ),
            MealEntity(
                id = 2,
                mealId = "124",
                date = testDate,
                mealType = "breakfast",
                food_name = "Toast",
                calories = 90,
                carbs = 15,
                protein = 3,
                fat = 2
            )
        )

        fakeApiService.setDailySummaryResponse(
            testDate,
            Response.error(500, "".toResponseBody(null))
        )
        fakeDao.setMeals(testDate, mockEntities)

        val result = mealTrackerRepository.getMealsByDate(testDate)

        assertTrue(result is Result.Success)
        val mealsData = (result as Result.Success).data
        assertEquals(1, mealsData.size)
        assertEquals(2, mealsData["breakfast"]?.size)
        assertFalse(fakeDao.insertMealsCalled)
        assertFalse(fakeDao.deleteMealsByDateCalled)
    }

    @Test
    fun deleteMealSuccess() = runTest {

        val testDate = "2025-04-14"
        val testFoodName = "Eggs"

        val mockDeleteResponse = DeleteMealResponse(
            message = "Meal deleted successfully",
            success = true
        )

        fakeApiService.setDeleteMealResponse(
            testDate,
            testFoodName,
            Response.success(mockDeleteResponse)
        )

        val result = mealTrackerRepository.deleteMeal(testDate, testFoodName)

        assertTrue(result is Result.Success)
        val success = (result as Result.Success).data
        assertTrue(success)
        assertEquals(testDate, fakeApiService.lastDeleteMealDate)
        assertEquals(testFoodName, fakeApiService.lastDeleteMealFoodName)
        assertTrue(fakeDao.deleteMealByDateAndNameCalled)
    }

    @Test
    fun deleteMealFailure() = runTest {
        val testDate = "2025-04-14"
        val testFoodName = "Eggs"

        val mockDeleteResponse = DeleteMealResponse(
            message = "Failed to delete meal",
            success = false
        )

        fakeApiService.setDeleteMealResponse(
            testDate,
            testFoodName,
            Response.success(mockDeleteResponse)
        )

        val result = mealTrackerRepository.deleteMeal(testDate, testFoodName)

        assertTrue(result is Result.Error)
        assertFalse(fakeDao.deleteMealByDateAndNameCalled)
    }

    @Test
    fun updateWaterIntakeDeltaSuccess() = runTest {
        val testDate = "2025-04-14"
        val delta = 0.5
        val newAmount = 2.5

        val mockWaterResponse = WaterResponse(
            date = testDate,
            water = 2.5
        )

        fakeApiService.setAdjustWaterResponse(mockWaterResponse)

        val result = mealTrackerRepository.updateWaterIntakeDelta(testDate, delta, newAmount)

        // Verify results
        assertEquals(2.5, result, 0.01)
        assertTrue(fakeDao.updateWaterIntakeCalled)
        assertEquals(testDate, fakeDao.lastUpdateWaterIntakeDate)
        assertEquals(newAmount, fakeDao.lastUpdateWaterIntakeAmount, 0.01)
    }

    @Test
    fun addMealSuccess() = runTest {
        val testDate = "2025-04-14"
        val mealType = "breakfast"
        val foodName = "Eggs"
        val calories = 150
        val carbs = 2
        val protein = 12
        val fat = 10

        val mockAddResponse = AddMealResponse(
            message = "Meal added successfully",
            mealId = "meal123"
        )

        fakeApiService.setAddMealResponse(Response.success(mockAddResponse))

        val result = mealTrackerRepository.addMeal(
            testDate,
            mealType,
            foodName,
            calories,
            carbs,
            protein,
            fat
        )

        assertTrue(result is Result.Success)
        val mealId = (result as Result.Success).data
        assertEquals("meal123", mealId)
        assertTrue(fakeApiService.addMealCalled)
    }

    @Test
    fun addMealFailure() = runTest {
        val testDate = "2025-04-14"
        val mealType = "breakfast"
        val foodName = "Eggs"
        val calories = 150
        val carbs = 2
        val protein = 12
        val fat = 10

        fakeApiService.setAddMealResponse(
            Response.error(
                400,
                okhttp3.ResponseBody.create(null, "")
            )
        )

        val result = mealTrackerRepository.addMeal(
            testDate,
            mealType,
            foodName,
            calories,
            carbs,
            protein,
            fat
        )

        assertTrue(result is Result.Error)
        assertTrue(fakeApiService.addMealCalled)
    }
}

class FakeMealTrackerApiService : MealTrackerApiService {

    private val dailySummaryResponses = mutableMapOf<String, Response<DailySummaryResponse>>()
    private var addMealResponse: Response<AddMealResponse>? = null
    private val deleteMealResponses =
        mutableMapOf<Pair<String, String>, Response<DeleteMealResponse>>()
    private var adjustWaterResponse: WaterResponse? = null
    private var shouldThrowNetworkError = false

    var lastGetDailySummaryDate: String? = null
    var lastDeleteMealDate: String? = null
    var lastDeleteMealFoodName: String? = null
    var addMealCalled = false

    fun setDailySummaryResponse(date: String, response: Response<DailySummaryResponse>) {
        dailySummaryResponses[date] = response
    }

    fun setAddMealResponse(response: Response<AddMealResponse>) {
        addMealResponse = response
    }

    fun setDeleteMealResponse(
        date: String,
        foodName: String,
        response: Response<DeleteMealResponse>
    ) {
        deleteMealResponses[Pair(date, foodName)] = response
    }

    fun setAdjustWaterResponse(response: WaterResponse) {
        adjustWaterResponse = response
    }

    fun setShouldThrowNetworkError(value: Boolean) {
        shouldThrowNetworkError = value
    }

    override suspend fun addMeal(mealRequest: AddMealRequest): Response<AddMealResponse> {
        if (shouldThrowNetworkError) {
            throw IOException("Network error")
        }
        addMealCalled = true
        return addMealResponse ?: Response.error(500, okhttp3.ResponseBody.create(null, ""))
    }

    override suspend fun adjustWater(data: AdjustWaterRequest): WaterResponse {
        if (shouldThrowNetworkError) {
            throw IOException("Network error")
        }
        return adjustWaterResponse ?: WaterResponse(data.date, data.delta)
    }

    override suspend fun getDailySummary(date: String): Response<DailySummaryResponse> {
        if (shouldThrowNetworkError) {
            throw IOException("Network error")
        }
        lastGetDailySummaryDate = date
        return dailySummaryResponses[date] ?: Response.error(
            404,
            okhttp3.ResponseBody.create(null, "")
        )
    }

    override suspend fun deleteMeal(date: String, foodName: String): Response<DeleteMealResponse> {
        if (shouldThrowNetworkError) {
            throw IOException("Network error")
        }
        lastDeleteMealDate = date
        lastDeleteMealFoodName = foodName
        return deleteMealResponses[Pair(date, foodName)] ?: Response.error(
            500,
            okhttp3.ResponseBody.create(null, "")
        )
    }

    override suspend fun getAllFoods(): Response<FoodResponse> {
        if (shouldThrowNetworkError) {
            throw IOException("Network error")
        }
        return Response.error(500, okhttp3.ResponseBody.create(null, ""))
    }
}

class FakeMealTrackerDao : MealTrackerDao {

    private val meals = mutableMapOf<String, List<MealEntity>>()
    private val dailySummaries = mutableMapOf<String, DailySummaryEntity?>()

    var insertDailySummaryCalled = false
    var insertMealsCalled = false
    var deleteMealsByDateCalled = false
    var deleteMealByDateAndNameCalled = false
    var updateWaterIntakeCalled = false
    var lastUpdateWaterIntakeDate: String? = null
    var lastUpdateWaterIntakeAmount: Double = 0.0

    fun setMeals(date: String, mealsList: List<MealEntity>) {
        meals[date] = mealsList
    }

    fun setDailySummary(date: String, summary: DailySummaryEntity?) {
        dailySummaries[date] = summary
    }

    override suspend fun insertMeal(meal: MealEntity): Long {
        return 1L
    }

    override suspend fun insertMeals(mealsList: List<MealEntity>) {
        insertMealsCalled = true
    }

    override fun getMealsByDate(date: String): Flow<List<MealEntity>> {
        return flowOf(meals[date] ?: emptyList())
    }

    override fun getMealsByDateAndType(date: String, mealType: String): Flow<List<MealEntity>> {
        val dateMeals = meals[date] ?: emptyList()
        return flowOf(dateMeals.filter { it.mealType == mealType })
    }

    override suspend fun deleteMealsByDate(date: String) {
        deleteMealsByDateCalled = true
    }

    override suspend fun insertDailySummary(summary: DailySummaryEntity) {
        insertDailySummaryCalled = true
        dailySummaries[summary.date] = summary
    }

    override fun getDailySummaryByDate(date: String): Flow<DailySummaryEntity?> {
        return flowOf(dailySummaries[date])
    }

    override suspend fun updateWaterIntake(date: String, waterLiters: Double) {
        updateWaterIntakeCalled = true
        lastUpdateWaterIntakeDate = date
        lastUpdateWaterIntakeAmount = waterLiters
    }

    override suspend fun updateDailySummaryWithMeals(
        summary: DailySummaryEntity,
        meals: List<MealEntity>
    ) {
        insertDailySummary(summary)
        insertMeals(meals)
    }

    override suspend fun deleteMealByDateAndName(date: String, food_name: String): Int {
        deleteMealByDateAndNameCalled = true
        return 1
    }
}