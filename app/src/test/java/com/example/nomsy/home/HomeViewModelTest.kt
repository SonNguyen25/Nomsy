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
import com.example.nomsy.data.remote.MealTrackerApiService
import com.example.nomsy.data.remote.NutritionTotals
import com.example.nomsy.data.remote.WaterResponse
import com.example.nomsy.data.repository.MealTrackerRepository
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response
import android.app.Application
import android.content.Context
import java.lang.reflect.Field

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeApi: FakeMealTrackerApiService
    private lateinit var fakeDao: FakeMealTrackerDao
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var testApplication: TestApplication

    private val testDate = "2025-04-14"
    private val testFormattedDate = "2025-04-14"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeApi = FakeMealTrackerApiService()
        fakeDao = FakeMealTrackerDao()

        val fakeRepo = MealTrackerRepository(
            mealApiService = fakeApi,
            mealDao = fakeDao
        )

        testApplication = TestApplication()
        homeViewModel = HomeViewModel(testApplication)

        val repoField = HomeViewModel::class.java
            .getDeclaredField("mealRepository")
            .apply { isAccessible = true }
        repoField.set(homeViewModel, fakeRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadingInitialDataTest() = runTest {
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
        fakeApi.setDailySummaryResponse(testDate, Response.success(mockSummaryResponse))

        advanceUntilIdle()

        val nutritionResult = homeViewModel.nutritionTotals.value
        assertNotNull("nutrition result should not be null", nutritionResult)
        assertTrue("nutrition should be Success", nutritionResult is Result.Success)
        val summaryEntity = (nutritionResult as Result.Success).data!!
        assertEquals(1500, summaryEntity.totalCalories)
        assertEquals(2.0, summaryEntity.waterLiters, 0.0)

        assertEquals(2.0, homeViewModel.waterIntake.value, 0.0)
    }

    @Test
    fun incrementDateTest() = runTest {
        assertEquals(14, homeViewModel.selectedDate.value)

        homeViewModel.incrementDate()
        advanceUntilIdle()
        assertEquals(14, homeViewModel.selectedDate.value)
    }

    @Test
    fun decrementDateTest() = runTest {
        assertEquals(14, homeViewModel.selectedDate.value)

        fakeApi.setDailySummaryResponse("2025-04-13", Response.success(
            DailySummaryResponse("2025-04-13", NutritionTotals(0,0,0,0,0.0), emptyMap())
        ))

        homeViewModel.decrementDate()
        advanceUntilIdle()

        assertEquals(13, homeViewModel.selectedDate.value)
    }

    @Test
    fun multipleDecrementTest() = runTest {
        for (d in 11..13) {
            val date = "2025-04-$d"
            fakeApi.setDailySummaryResponse(
                date,
                Response.success(DailySummaryResponse(date, NutritionTotals(0,0,0,0,0.0), emptyMap()))
            )
        }

        repeat(4) {
            homeViewModel.decrementDate()
            advanceUntilIdle()
        }
        assertEquals(11, homeViewModel.selectedDate.value)

        homeViewModel.decrementDate()
        advanceUntilIdle()
        assertEquals(11, homeViewModel.selectedDate.value)
    }

    @Test
    fun updateWaterIntakeTest() = runTest {
        fakeApi.setDailySummaryResponse(
            testDate,
            Response.success(DailySummaryResponse(testDate, NutritionTotals(0,0,0,0,2.0), emptyMap()))
        )
        advanceUntilIdle()
        assertEquals(2.0, homeViewModel.waterIntake.value, 0.0)

        fakeApi.setAdjustWaterResponse(WaterResponse(testDate, 3.5))
        homeViewModel.updateWaterIntake(testDate, 3.5)
        advanceUntilIdle()

        assertEquals(testDate, fakeDao.lastUpdateDate)
        assertEquals(3.5, fakeDao.lastUpdateAmount, 0.0)
        assertEquals(3.5, homeViewModel.waterIntake.value, 0.0)
    }

    @Test
    fun deleteMealTest() = runTest {
        fakeApi.setDeleteMealResponse(
            testDate, "Eggs",
            Response.success(DeleteMealResponse("ok", true))
        )

        homeViewModel.deleteMeal(testDate, "Eggs")
        advanceUntilIdle()

        assertTrue(fakeDao.deleteByDateAndNameCalled)
    }


    @Test
    fun refreshDataTest() = runTest {
        fakeDao.clearSummaries()
        fakeApi.setDailySummaryResponse(
            testDate,
            Response.success(DailySummaryResponse(testDate, NutritionTotals(5,5,5,5,0.5), emptyMap()))
        )

        homeViewModel.refreshData()
        advanceUntilIdle()

        val loaded = fakeDao.getDailySummaryByDate(testDate).first()
        assertNotNull(loaded)
        assertEquals(5, loaded!!.totalCalories)
    }

    class TestApplication : Application() {
        override fun getApplicationContext(): Context = this
    }
}

class FakeMealTrackerApiService : MealTrackerApiService {
    private val dailyMap = mutableMapOf<String, Response<DailySummaryResponse>>()
    private val deleteMap = mutableMapOf<Pair<String,String>, Response<DeleteMealResponse>>()
    private var waterResp = WaterResponse("", 0.0)

    fun setDailySummaryResponse(date: String, resp: Response<DailySummaryResponse>) {
        dailyMap[date] = resp
    }

    fun setDeleteMealResponse(date: String, food: String, resp: Response<DeleteMealResponse>) {
        deleteMap[Pair(date,food)] = resp
    }

    fun setAdjustWaterResponse(resp: WaterResponse) {
        waterResp = resp
    }

    override suspend fun getDailySummary(date: String): Response<DailySummaryResponse> =
        dailyMap[date] ?: Response.error(404, "".toResponseBody())

    override suspend fun deleteMeal(date: String, foodName: String): Response<DeleteMealResponse> =
        deleteMap[Pair(date,foodName)] ?: Response.error(500, "".toResponseBody())

    override suspend fun adjustWater(data: AdjustWaterRequest): WaterResponse =
        waterResp

    override suspend fun addMeal(mealRequest: AddMealRequest): Response<AddMealResponse> =
        Response.success(AddMealResponse("ok","id"))

    override suspend fun getAllFoods(): Response<FoodResponse> =
        Response.success(FoodResponse(emptyList()))
}

class FakeMealTrackerDao : MealTrackerDao {
    private val summaryMap = mutableMapOf<String, DailySummaryEntity?>()
    var lastUpdateDate: String? = null
    var lastUpdateAmount: Double = 0.0
    var deleteByDateAndNameCalled = false

    fun clearSummaries() = summaryMap.clear()

    override suspend fun insertDailySummary(summary: DailySummaryEntity) {
        summaryMap[summary.date] = summary
    }

    override fun getDailySummaryByDate(date: String) =
        flowOf(summaryMap[date])

    override suspend fun updateWaterIntake(date: String, waterLiters: Double) {
        lastUpdateDate = date
        lastUpdateAmount = waterLiters
    }

    override suspend fun deleteMealByDateAndName(date: String, food_name: String): Int {
        deleteByDateAndNameCalled = true
        return 1
    }

    override suspend fun insertMeal(meal: MealEntity): Long = 1L

    override suspend fun insertMeals(meals: List<MealEntity>) {}

    override fun getMealsByDate(date: String) = flowOf(emptyList<MealEntity>())

    override fun getMealsByDateAndType(date: String, mealType: String) =
        flowOf(emptyList<MealEntity>())

    override suspend fun deleteMealsByDate(date: String) {}

    override suspend fun updateDailySummaryWithMeals(
        summary: DailySummaryEntity,
        meals: List<MealEntity>
    ) {}
}