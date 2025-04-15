package com.example.nomsy.home

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
import com.example.nomsy.data.remote.WaterResponse
import com.example.nomsy.data.repository.IMealTrackerRepository
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeMealTrackerRepository: FakeMealTrackerRepository
    private lateinit var homeViewModel: HomeViewModel

    private val testDate = "2025-04-14"
    private val testFormattedDate = "2025-04-14"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeMealTrackerRepository = FakeMealTrackerRepository()

        val application = org.robolectric.RuntimeEnvironment.getApplication()
        homeViewModel = HomeViewModel(application)


        val field: Field = HomeViewModel::class.java.getDeclaredField("mealRepository")
        field.isAccessible = true
        field.set(homeViewModel, fakeMealTrackerRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadingInitialDataTest() = runTest {
        val mockSummary = DailySummaryEntity(
            date = testFormattedDate,
            totalCalories = 1500,
            totalCarbs = 150,
            totalProtein = 90,
            totalFat = 50,
            waterLiters = 2.0
        )

        val mealItems = mapOf(
            "breakfast" to listOf(
                MealItem("Eggs", 150, 2, 12, 10),
                MealItem("Toast", 90, 15, 3, 2)
            )
        )

        fakeMealTrackerRepository.setDailyNutritionTotalsResult(
            testFormattedDate,
            Result.Success(flowOf(mockSummary))
        )
        fakeMealTrackerRepository.setMealsByDateResult(
            testFormattedDate,
            Result.Success(mealItems)
        )
        fakeMealTrackerRepository.setUpdateWaterIntakeDeltaResult(2.0)

        advanceUntilIdle()

        assertEquals(testFormattedDate, fakeMealTrackerRepository.lastLoadedDate)

        val nutritionResult = homeViewModel.nutritionTotals.value
        assertNotNull("nutrition result should not be null", nutritionResult)
        assertTrue(
            "nutrition result should be success but was $nutritionResult",
            nutritionResult is Result.Success
        )
        assertEquals(mockSummary, (nutritionResult as Result.Success).data)

        val mealsResult = homeViewModel.mealsByType.value
        assertNotNull("meals result should not be null", mealsResult)
        assertTrue(
            "meals result should be success but was $mealsResult",
            mealsResult is Result.Success
        )
        assertEquals(mealItems, (mealsResult as Result.Success).data)

        assertEquals(2.0, homeViewModel.waterIntake.value, 0.01)
    }

    @Test
    fun incrementDateTest() = runTest {
        assertEquals(14, homeViewModel.selectedDate.value)

        fakeMealTrackerRepository.lastLoadedDate = null

        homeViewModel.incrementDate()
        advanceUntilIdle()

        assertEquals(14, homeViewModel.selectedDate.value)
        assertEquals(null, fakeMealTrackerRepository.lastLoadedDate)
    }

    @Test
    fun decrementDateTest() = runTest {
        assertEquals(14, homeViewModel.selectedDate.value)

        fakeMealTrackerRepository.lastLoadedDate = null

        homeViewModel.decrementDate()
        advanceUntilIdle()

        assertEquals(13, homeViewModel.selectedDate.value)
        assertEquals("2025-04-13", fakeMealTrackerRepository.lastLoadedDate)
    }

    @Test
    fun multipleDecrementTest() = runTest {
        assertEquals(14, homeViewModel.selectedDate.value)

        repeat(4) {
            homeViewModel.decrementDate()
            advanceUntilIdle()
        }

        assertEquals(11, homeViewModel.selectedDate.value)
        assertEquals("2025-04-11", fakeMealTrackerRepository.lastLoadedDate)

        homeViewModel.decrementDate()
        advanceUntilIdle()

        assertEquals(11, homeViewModel.selectedDate.value)
    }

    @Test
    fun updateWaterIntakeTest() = runTest {
        val newWaterAmount = 3.0
        fakeMealTrackerRepository.setUpdateWaterIntakeDeltaResult(3.0)

        advanceUntilIdle()
        assertEquals(2.0, homeViewModel.waterIntake.value, 0.01)

        homeViewModel.updateWaterIntake(testFormattedDate, newWaterAmount)
        advanceUntilIdle()

        assertEquals(testFormattedDate, fakeMealTrackerRepository.lastUpdateWaterIntakeDeltaDate)
        assertEquals(1.0, fakeMealTrackerRepository.lastUpdateWaterIntakeDelta, 0.01)
        assertEquals(3.0, fakeMealTrackerRepository.lastUpdateWaterIntakeNewAmount, 0.01)

        assertEquals(3.0, homeViewModel.waterIntake.value, 0.01)
    }

    @Test
    fun deleteMealTest() = runTest {
        val foodName = "Eggs"
        fakeMealTrackerRepository.setDeleteMealResult(
            testFormattedDate,
            foodName,
            Result.Success(true)
        )

        fakeMealTrackerRepository.lastLoadedDate = null

        homeViewModel.deleteMeal(testFormattedDate, foodName)
        advanceUntilIdle()

        assertEquals(testFormattedDate, fakeMealTrackerRepository.lastDeleteMealDate)
        assertEquals(foodName, fakeMealTrackerRepository.lastDeleteMealFoodName)

        assertEquals(testFormattedDate, fakeMealTrackerRepository.lastLoadedDate)
    }


    @Test
    fun refreshDataTest() = runTest {
        fakeMealTrackerRepository.lastLoadedDate = null

        homeViewModel.refreshData()
        advanceUntilIdle()

        assertEquals(testFormattedDate, fakeMealTrackerRepository.lastLoadedDate)
    }
}

class FakeMealTrackerRepository : IMealTrackerRepository {

    private val dailyNutritionTotalsResults =
        mutableMapOf<String, Result<Flow<DailySummaryEntity?>>>()
    private val mealsByDateResults = mutableMapOf<String, Result<Map<String, List<MealItem>>>>()
    private val deleteMealResults = mutableMapOf<Pair<String, String>, Result<Boolean>>()
    private val addMealResults = mutableMapOf<String, Result<String>>()
    private var updateWaterIntakeDeltaResult = 0.0

    var lastLoadedDate: String? = null
    var lastDeleteMealDate: String? = null
    var lastDeleteMealFoodName: String? = null
    var lastUpdateWaterIntakeDeltaDate: String? = null
    var lastUpdateWaterIntakeDelta: Double = 0.0
    var lastUpdateWaterIntakeNewAmount: Double = 0.0
    var lastAddMealDate: String? = null
    var lastAddMealType: String? = null
    var lastAddMealFoodName: String? = null
    var lastAddMealCalories: Int? = null
    var lastAddMealCarbs: Int? = null
    var lastAddMealProtein: Int? = null
    var lastAddMealFat: Int? = null

    fun setDailyNutritionTotalsResult(date: String, result: Result<Flow<DailySummaryEntity?>>) {
        dailyNutritionTotalsResults[date] = result
    }

    fun setMealsByDateResult(date: String, result: Result<Map<String, List<MealItem>>>) {
        mealsByDateResults[date] = result
    }

    fun setDeleteMealResult(date: String, foodName: String, result: Result<Boolean>) {
        deleteMealResults[Pair(date, foodName)] = result
    }

    fun setAddMealResult(date: String, result: Result<String>) {
        addMealResults[date] = result
    }

    fun setUpdateWaterIntakeDeltaResult(result: Double) {
        updateWaterIntakeDeltaResult = result
    }

    override suspend fun getDailyNutritionTotals(date: String): Result<Flow<DailySummaryEntity?>> {
        lastLoadedDate = date
        return dailyNutritionTotalsResults[date]
            ?: Result.Error(Exception("no test result set for getDailyNutritionTotals on date $date"))
    }

    override suspend fun getMealsByDate(date: String): Result<Map<String, List<MealItem>>> {
        lastLoadedDate = date
        return mealsByDateResults[date]
            ?: Result.Error(Exception("no test result set for getMealsByDate on date $date"))
    }

    override suspend fun deleteMeal(date: String, foodName: String): Result<Boolean> {
        lastDeleteMealDate = date
        lastDeleteMealFoodName = foodName
        return deleteMealResults[Pair(date, foodName)]
            ?: Result.Error(Exception("no test result set for deleteMeal on date $date and food $foodName"))
    }

    override suspend fun updateWaterIntakeDelta(
        date: String,
        delta: Double,
        newAmount: Double
    ): Double {
        lastUpdateWaterIntakeDeltaDate = date
        lastUpdateWaterIntakeDelta = delta
        lastUpdateWaterIntakeNewAmount = newAmount
        return updateWaterIntakeDeltaResult
    }

    override suspend fun addMeal(
        date: String,
        mealType: String,
        foodName: String,
        calories: Int,
        carbs: Int,
        protein: Int,
        fat: Int
    ): Result<String> {
        lastAddMealDate = date
        lastAddMealType = mealType
        lastAddMealFoodName = foodName
        lastAddMealCalories = calories
        lastAddMealCarbs = carbs
        lastAddMealProtein = protein
        lastAddMealFat = fat

        return addMealResults[date] ?: Result.Success("meal-id-123")
    }
}

class FakeMealTrackerApiService : MealTrackerApiService {
    override suspend fun addMeal(mealRequest: AddMealRequest): Response<AddMealResponse> {
        return Response.success(AddMealResponse("success", "meal-123"))
    }

    override suspend fun adjustWater(data: AdjustWaterRequest): WaterResponse {
        return WaterResponse(data.date, data.delta)
    }

    override suspend fun getDailySummary(date: String): Response<DailySummaryResponse> {
        return Response.success(null)
    }

    override suspend fun deleteMeal(date: String, foodName: String): Response<DeleteMealResponse> {
        return Response.success(DeleteMealResponse("success", true))
    }

    override suspend fun getAllFoods(): Response<FoodResponse> {
        return Response.success(null)
    }
}

class FakeMealTrackerDao : MealTrackerDao {
    override suspend fun insertMeal(meal: MealEntity): Long {
        return 1L
    }

    override suspend fun insertMeals(meals: List<MealEntity>) {
    }

    override fun getMealsByDate(date: String): Flow<List<MealEntity>> {
        return flowOf(emptyList())
    }

    override fun getMealsByDateAndType(date: String, mealType: String): Flow<List<MealEntity>> {
        return flowOf(emptyList())
    }

    override suspend fun deleteMealsByDate(date: String) {
    }

    override suspend fun insertDailySummary(summary: DailySummaryEntity) {
    }

    override fun getDailySummaryByDate(date: String): Flow<DailySummaryEntity?> {
        return flowOf(null)
    }

    override suspend fun updateWaterIntake(date: String, waterLiters: Double) {
    }

    override suspend fun updateDailySummaryWithMeals(
        summary: DailySummaryEntity,
        meals: List<MealEntity>
    ) {
    }

    override suspend fun deleteMealByDateAndName(date: String, food_name: String): Int {
        return 1
    }
}