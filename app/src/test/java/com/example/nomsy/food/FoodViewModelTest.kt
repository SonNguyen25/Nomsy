package com.example.nomsy.food

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.nomsy.data.local.models.Food
import com.example.nomsy.data.remote.*
import com.example.nomsy.data.repository.FoodRepository
import com.example.nomsy.data.remote.MealTrackerRetrofitClient
import com.example.nomsy.testutil.getOrAwaitValue
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.FoodViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.RobolectricTestRunner
import retrofit2.Response
import java.lang.reflect.Field

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class FoodViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mainDispatcher = UnconfinedTestDispatcher()

    private lateinit var fakeApi: FakeMealTrackerApiService
    private lateinit var vm: FoodViewModel

    @Before
    fun setup() {

        Dispatchers.setMain(mainDispatcher)


        fakeApi = FakeMealTrackerApiService()


        val clientClass = MealTrackerRetrofitClient::class.java
        val delegateField: Field = clientClass.getDeclaredField("mealTrackerApi\$delegate")
            .apply { isAccessible = true }
        val lazyImpl = delegateField.get(null)!!
        val valueField: Field = lazyImpl.javaClass.getDeclaredField("_value")
            .apply { isAccessible = true }
        valueField.set(lazyImpl, fakeApi)


        vm = FoodViewModel(RuntimeEnvironment.getApplication())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun searchFoodsFromApiFiltersByQuery() = runTest {

        val apple = Food(
            "1", "2025-05-14", 50.toString(), "Apple", 0, 0, 0,
            fat = 10
        )
        val banana = Food(
            "2", "2025-05-14", 100.toString(), "Banana", 1, 0, 0,
            fat = 2
        )
        fakeApi.getAllFoodsResponse = Response.success(FoodResponse(listOf(apple, banana)))


        vm.searchFoodsFromApi("app")

        assertEquals(listOf(apple), vm.searchResults)
    }

    @Test
    fun fetchAllFoodsPopulatesBothLists() = runTest {
        val f1 = Food("1", "2025-05-14", "breakfast", "Carrot", 1, 0, 0, 0)
        val f2 = Food("2", "2025-05-14", "lunch", "Broccoli", 4, 1, 0, 0)
        fakeApi.getAllFoodsResponse = Response.success(FoodResponse(listOf(f1, f2)))

        vm.fetchAllFoods()

        assertEquals(listOf(f1, f2), vm.allFoods)
        assertEquals(listOf(f1, f2), vm.searchResults)
    }

    @Test
    fun fetchDailySummaryPostsTotals() = runTest {
        val totals = NutritionTotals(2000, 250, 100, 70, 3.0)
        fakeApi.dailySummaryResponse = Response.success(
            DailySummaryResponse("2025-04-15", totals, emptyMap())
        )

        vm.fetchDailySummary("2025-04-15")

        val result = vm.dailySummary.getOrAwaitValue()
        assertEquals(totals, result)
    }

    @Test
    fun submitMealSuccessEmitsResult() = runTest {
        val req = AddMealRequest("2025-04-15", "dinner", "Steak", 600, 0, 40, 20)
        val resp = AddMealResponse("OK", "id-123")
        fakeApi.addMealResponse = Response.success(resp)

        vm.submitMeal(req)
        val result = vm.mealResult!!.getOrAwaitValue()

        assertTrue(result is Result.Success)
        assertEquals(resp, (result as Result.Success).data)
    }

    @Test
    fun submitMealErrorEmitsError() = runTest {
        val req = AddMealRequest("2025-04-15", "dinner", "Steak", 600, 0, 40, 20)
        val msg = "Bad request"
        fakeApi.addMealResponse = Response.error(
            400,
            msg.toResponseBody("application/json".toMediaTypeOrNull())
        )

        vm.submitMeal(req)
        val result = vm.mealResult!!.getOrAwaitValue()

        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error

        assertTrue(errorResult.exception.message!!.contains("Failed to add meal"))
        assertTrue(errorResult.exception.message!!.contains(msg))

    }

    @Test
    fun clearMealResultResetsLiveData() = runTest {

        fakeApi.addMealResponse = Response.success(AddMealResponse("OK", "x"))
        vm.submitMeal(AddMealRequest("d","t","f",1,1,1,1))
        vm.mealResult!!.getOrAwaitValue()  // consume


        vm.clearMealResult()
        assertNull(vm.mealResult!!.value)
    }
}


//
//class FakeMealTrackerApiService : MealTrackerApiService {
//    var getAllFoodsResponse: Response<FoodResponse>? = null
//    var dailySummaryResponse: Response<DailySummaryResponse>? = null
//    var addMealResponse: Response<AddMealResponse>? = null
//    var deleteMealResponse: Response<DeleteMealResponse>? = null
//    var adjustWaterResponse: WaterResponse? = null
//
//    override suspend fun getAllFoods(): Response<FoodResponse> =
//        getAllFoodsResponse
//            ?: Response.success(FoodResponse(emptyList()))
//
//    override suspend fun getDailySummary(date: String): Response<DailySummaryResponse> =
//        dailySummaryResponse
//            ?: Response.success(
//                DailySummaryResponse(
//                    date, NutritionTotals(0,0,0,0,0.0), emptyMap()
//                )
//            )
//
//    override suspend fun addMeal(mealRequest: AddMealRequest): Response<AddMealResponse> {
//        return addMealResponse
//            ?: Response.error(
//                500,
//                "No addMealResponse".toResponseBody("application/json".toMediaTypeOrNull())
//            )
//    }
//
//    override suspend fun deleteMeal(date: String, foodName: String): Response<DeleteMealResponse> =
//        deleteMealResponse
//            ?: Response.success(DeleteMealResponse("ok", true))
//
//    override suspend fun adjustWater(data: AdjustWaterRequest): WaterResponse =
//        adjustWaterResponse
//            ?: WaterResponse(data.date, data.delta)
//}
