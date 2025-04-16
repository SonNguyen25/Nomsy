package com.example.nomsy.food

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.nomsy.data.local.entities.Food
import com.example.nomsy.data.remote.*
import com.example.nomsy.data.remote.MealTrackerRetrofitClient
import com.example.nomsy.testutil.getOrAwaitValue
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.FoodViewModel
import junit.framework.TestCase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response
import java.lang.reflect.Field

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class FoodViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeApi: FakeMealTrackerApiService
    private lateinit var viewModel: FoodViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeApi = FakeMealTrackerApiService()

        // Inject the fakeApi into the MealTrackerRetrofitClient singleton via reflection
        val clientClass = MealTrackerRetrofitClient::class.java
        val delegateField: Field = clientClass.getDeclaredField("mealTrackerApi\$delegate")
            .apply { isAccessible = true }
        val lazyImpl = delegateField.get(null)!!
        val valueField: Field = lazyImpl.javaClass.getDeclaredField("_value")
            .apply { isAccessible = true }
        valueField.set(lazyImpl, fakeApi)

        val app = Application()
        viewModel = FoodViewModel(app)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun searchFoodsFromApiFiltersByQuery() = runTest {
        val apple = Food("1", "2025-05-14", "50", "Apple", 0, 0, 0, fat = 10)
        val banana = Food("2", "2025-05-14", "100", "Banana", 1, 0, 0, fat = 2)
        fakeApi.getAllFoodsResponse = Response.success(FoodResponse(listOf(apple, banana)))

        viewModel.searchFoodsFromApi("app")

        assertEquals(listOf(apple), viewModel.searchResults)
    }

    @Test
    fun fetchAllFoodsPopulatesBothLists() = runTest {
        val f1 = Food("1", "2025-05-14", "breakfast", "Carrot", 1, 0, 0, 0)
        val f2 = Food("2", "2025-05-14", "lunch", "Broccoli", 4, 1, 0, 0)
        fakeApi.getAllFoodsResponse = Response.success(FoodResponse(listOf(f1, f2)))

        viewModel.fetchAllFoods()

        assertEquals(listOf(f1, f2), viewModel.allFoods)
        assertEquals(listOf(f1, f2), viewModel.searchResults)
    }

    @Test
    fun fetchDailySummaryPostsTotals() = runTest {
        val totals = NutritionTotals(2000, 250, 100, 70, 3.0)
        fakeApi.dailySummaryResponse = Response.success(
            DailySummaryResponse("2025-04-15", totals, emptyMap())
        )

        viewModel.fetchDailySummary("2025-04-15")

        val result = viewModel.dailySummary.getOrAwaitValue()
        assertEquals(totals, result)
    }

    @Test
    fun submitMealSuccessEmitsResult() = runTest {
        val req = AddMealRequest("2025-04-15", "dinner", "Steak", 600, 0, 40, 20)
        val resp = AddMealResponse("OK", "id-123")
        fakeApi.addMealResponse = Response.success(resp)

        viewModel.submitMeal(req)
        val result = viewModel.mealResult!!.getOrAwaitValue()

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

        viewModel.submitMeal(req)
        val result = viewModel.mealResult!!.getOrAwaitValue()

        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertTrue(errorResult.exception.message!!.contains("Failed to add meal"))
        assertTrue(errorResult.exception.message!!.contains(msg))
    }

    @Test
    fun clearMealResultResetsLiveData() = runTest {
        fakeApi.addMealResponse = Response.success(AddMealResponse("OK", "x"))
        viewModel.submitMeal(AddMealRequest("d", "t", "f", 1, 1, 1, 1))
        viewModel.mealResult!!.getOrAwaitValue()

        viewModel.clearMealResult()
        assertNull(viewModel.mealResult!!.value)
    }
}
