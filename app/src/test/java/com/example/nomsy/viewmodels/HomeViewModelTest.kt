package com.example.nomsy.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.nomsy.data.local.entities.DailySummaryEntity
import com.example.nomsy.data.remote.MealItem
import com.example.nomsy.data.repository.IMealTrackerRepository
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    class TestApplication : Application()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var application: TestApplication
    private lateinit var fakeRepository: FakeMealTrackerRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        application = TestApplication()
        fakeRepository = FakeMealTrackerRepository()
        val testDate = "2025-04-14"
        val mockSummary = DailySummaryEntity(
            date = testDate,
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
        fakeRepository.setDailyNutritionTotals(testDate, Result.Success(flowOf(mockSummary)))
        fakeRepository.setMealsByDate(testDate, Result.Success(mealItems))
        fakeRepository.setUpdateWaterIntakeDeltaResult(2.5)

        viewModel = HomeViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun loadDefaultStateData() = runTest {
        // Default date in the ViewModel is 14
        val expectedDate = "2025-04-14"
        // Assert
        assertEquals(14, viewModel.selectedDate.value)
        assertEquals(expectedDate, fakeRepository.lastGetDailyNutritionTotalsDate)
        assertEquals(expectedDate, fakeRepository.lastGetMealsByDateDate)

        // Verify nutrition totals were updated
        val nutritionValue = viewModel.nutritionTotals.getOrAwaitValue()
        assertTrue(nutritionValue is Result.Success)

        // Verify meals were updated
        val mealsValue = viewModel.mealsByType.getOrAwaitValue()
        assertTrue(mealsValue is Result.Success)
    }

    @Test
    fun `incrementDate changes selected date and loads new data`() = runTest {
        // Reset tracking fields
        fakeRepository.lastGetDailyNutritionTotalsDate = null
        fakeRepository.lastGetMealsByDateDate = null

        // Act
        viewModel.incrementDate()
        testDispatcher.scheduler.advanceUntilIdle() // Advance coroutines

        // Assert
        assertEquals(14, viewModel.selectedDate.value) // It's already at max (14)

        // Since we're at max, we shouldn't load new data
        assertNull(fakeRepository.lastGetDailyNutritionTotalsDate)
        assertNull(fakeRepository.lastGetMealsByDateDate)
    }

    @Test
    fun `decrementDate changes selected date and loads new data`() = runTest {
        // Reset tracking fields
        fakeRepository.lastGetDailyNutritionTotalsDate = null
        fakeRepository.lastGetMealsByDateDate = null

        viewModel.decrementDate()
        testDispatcher.scheduler.advanceUntilIdle() // Advance coroutines

        assertEquals(13, viewModel.selectedDate.value)
        assertEquals("2025-04-13", fakeRepository.lastGetDailyNutritionTotalsDate)
        assertEquals("2025-04-13", fakeRepository.lastGetMealsByDateDate)
    }

    @Test
    fun `decrementDate does not go below minimum date`() = runTest {
        repeat(3) {
            viewModel.decrementDate()
            testDispatcher.scheduler.advanceUntilIdle()
        }
        assertEquals(11, viewModel.selectedDate.value)

        // Reset
        fakeRepository.lastGetDailyNutritionTotalsDate = null
        fakeRepository.lastGetMealsByDateDate = null

        // try to decrement below minimum
        viewModel.decrementDate()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(11, viewModel.selectedDate.value)
        // tests latest still there
        assertNotNull(fakeRepository.lastGetDailyNutritionTotalsDate)
        assertNotNull(fakeRepository.lastGetMealsByDateDate)
    }

    @Test
    fun `updateWaterIntake updates water intake value`() = runTest {
        val testDate = "2025-04-14"
        val newWaterAmount = 3.0

        viewModel.updateWaterIntake(testDate, newWaterAmount)
        testDispatcher.scheduler.advanceUntilIdle() // Advance coroutines

        assertEquals(testDate, fakeRepository.lastUpdateWaterIntakeDeltaDate)
        assertEquals(3.0, fakeRepository.lastUpdateWaterIntakeNewAmount, 0.01)
        assertEquals(
            2.5,
            viewModel.waterIntake.value,
            0.01
        ) // Should match fake repository return value
    }

    @Test
    fun `deleteMeal calls repository and reloads data`() = runTest {
        val testDate = "2025-04-14"
        val testFoodName = "Eggs"

        fakeRepository.setDeleteMealResult(testDate, testFoodName, Result.Success(true))

        // reset tracking fields
        fakeRepository.lastGetDailyNutritionTotalsDate = null
        fakeRepository.lastGetMealsByDateDate = null

        viewModel.deleteMeal(testDate, testFoodName)
        testDispatcher.scheduler.advanceUntilIdle() // Advance coroutines

        assertEquals(testDate, fakeRepository.lastDeleteMealDate)
        assertEquals(testFoodName, fakeRepository.lastDeleteMealFoodName)
        assertEquals(testDate, fakeRepository.lastGetDailyNutritionTotalsDate)
        assertEquals(testDate, fakeRepository.lastGetMealsByDateDate)
    }

    @Test
    fun `refreshData reloads all data`() = runTest {
        fakeRepository.lastGetDailyNutritionTotalsDate = null
        fakeRepository.lastGetMealsByDateDate = null
        viewModel.refreshData()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("2025-04-14", fakeRepository.lastGetDailyNutritionTotalsDate)
        assertEquals("2025-04-14", fakeRepository.lastGetMealsByDateDate)
    }
}

class FakeMealTrackerRepository : IMealTrackerRepository {
    private val dailyNutritionTotalsMap = mutableMapOf<String, Result<Flow<DailySummaryEntity?>>>()
    private val mealsByDateMap = mutableMapOf<String, Result<Map<String, List<MealItem>>>>()
    private val deleteMealResultMap = mutableMapOf<Pair<String, String>, Result<Boolean>>()
    private var updateWaterIntakeDeltaResult: Double = 0.0
    private val addMealResultMap = mutableMapOf<String, Result<String>>()

    var lastGetDailyNutritionTotalsDate: String? = null
    var lastGetMealsByDateDate: String? = null
    var lastDeleteMealDate: String? = null
    var lastDeleteMealFoodName: String? = null
    var lastUpdateWaterIntakeDeltaDate: String? = null
    var lastUpdateWaterIntakeDelta: Double = 0.0
    var lastUpdateWaterIntakeNewAmount: Double = 0.0
    var lastAddMealDate: String? = null

    fun setDailyNutritionTotals(date: String, result: Result<Flow<DailySummaryEntity?>>) {
        dailyNutritionTotalsMap[date] = result
    }

    fun setMealsByDate(date: String, result: Result<Map<String, List<MealItem>>>) {
        mealsByDateMap[date] = result
    }

    fun setDeleteMealResult(date: String, foodName: String, result: Result<Boolean>) {
        deleteMealResultMap[Pair(date, foodName)] = result
    }

    fun setUpdateWaterIntakeDeltaResult(result: Double) {
        updateWaterIntakeDeltaResult = result
    }

    fun setAddMealResult(date: String, result: Result<String>) {
        addMealResultMap[date] = result
    }

    override suspend fun getDailyNutritionTotals(date: String): Result<Flow<DailySummaryEntity?>> {
        lastGetDailyNutritionTotalsDate = date
        return dailyNutritionTotalsMap[date] ?: Result.Error(Exception("No data for date: $date"))
    }

    override suspend fun getMealsByDate(date: String): Result<Map<String, List<MealItem>>> {
        lastGetMealsByDateDate = date
        return mealsByDateMap[date] ?: Result.Error(Exception("No meals for date: $date"))
    }

    override suspend fun deleteMeal(date: String, foodName: String): Result<Boolean> {
        lastDeleteMealDate = date
        lastDeleteMealFoodName = foodName
        return deleteMealResultMap[Pair(date, foodName)]
            ?: Result.Error(Exception("Delete meal failed"))
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
        return addMealResultMap[date] ?: Result.Error(Exception("Add meal failed"))
    }
}
