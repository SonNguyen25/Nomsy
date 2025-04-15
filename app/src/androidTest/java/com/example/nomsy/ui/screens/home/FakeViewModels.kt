import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nomsy.data.local.entities.DailySummaryEntity
import com.example.nomsy.data.local.models.User
import com.example.nomsy.data.remote.MealItem
import com.example.nomsy.data.remote.UpdateProfileRequest
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.IAuthViewModel
import com.example.nomsy.viewModels.IHomeViewModel
import com.example.nomsy.viewModels.IProfileViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeHomeViewModel : IHomeViewModel {
    private val _nutritionTotals = MutableLiveData<Result<DailySummaryEntity?>>()
    override val nutritionTotals: LiveData<Result<DailySummaryEntity?>> = _nutritionTotals
    private val _mealsByType = MutableLiveData<Result<Map<String, List<MealItem>>>>()
    override val mealsByType: LiveData<Result<Map<String, List<MealItem>>>> = _mealsByType

    private val _waterIntake = MutableStateFlow(0.0)
    override val waterIntake: StateFlow<Double> = _waterIntake

    private val _selectedDate = MutableStateFlow(12)
    override val selectedDate: StateFlow<Int> = _selectedDate

    // tracking last deleted meal FOR TESTS
    var lastDeletedMeal: String? = null
    var lastDeletedDate: String? = null

    init {
        // initialize defaults here
        _waterIntake.value = 0.0
    }

    fun setNutritionTotals(result: Result<DailySummaryEntity?>) {
        _nutritionTotals.value = result
    }

    fun setMealsByType(result: Result<Map<String, List<MealItem>>>) {
        _mealsByType.value = result
    }

    fun setWaterIntake(value: Double) {
        _waterIntake.value = value
    }

    override fun incrementDate() {
        if (_selectedDate.value < 13) {
            _selectedDate.value = _selectedDate.value + 1
        }
    }

    override fun decrementDate() {
        if (_selectedDate.value > 11) {
            _selectedDate.value = _selectedDate.value - 1
        }
    }

    override fun updateWaterIntake(date: String, newWaterIntake: Double) {
        _waterIntake.value = newWaterIntake
    }

    override fun deleteMeal(date: String, foodName: String) {
        lastDeletedDate = date
        lastDeletedMeal = foodName

        val currentMeals = (_mealsByType.value as? Result.Success)?.data ?: emptyMap()
        val updatedMeals = currentMeals.mapValues { (_, meals) ->
            meals.filter { it.food_name != foodName }
        }.filter { it.value.isNotEmpty() }

        _mealsByType.value = Result.Success(updatedMeals)
    }
}

class FakeAuthViewModel : IAuthViewModel {
    private var currentUsername = ""
    override val loginResult: LiveData<Result<User>?>
        get() = TODO("Not yet implemented")
    override val isLoggedIn: StateFlow<Boolean>
        get() = TODO("Not yet implemented")
    override val registerResult: LiveData<Result<User>>
        get() = TODO("Not yet implemented")
    override val profileResult: LiveData<Result<User>>
        get() = TODO("Not yet implemented")

    override fun setCurrentUsername(username: String) {
        currentUsername = username
    }

    override fun getCurrentUsername(): String {
        return currentUsername
    }

    override fun login(username: String, password: String) {
        TODO("Not yet implemented")
    }

    override fun logout() {
        TODO("Not yet implemented")
    }

    override fun register(user: User) {
        TODO("Not yet implemented")
    }

    override fun fetchProfile(userId: String) {
        TODO("Not yet implemented")
    }

    override fun fetchProfileByUsername(username: String) {
        TODO("Not yet implemented")
    }

    override fun setCredentials(username: String, password: String, email: String) {
        TODO("Not yet implemented")
    }

    override fun setUserName(name: String) {
        TODO("Not yet implemented")
    }

    override fun setUserAge(age: Int) {
        TODO("Not yet implemented")
    }

    override fun setUserHeight(height: Int) {
        TODO("Not yet implemented")
    }

    override fun setUserWeight(weight: Int) {
        TODO("Not yet implemented")
    }

    override fun setUserFitnessGoal(goal: String) {
        TODO("Not yet implemented")
    }

    override fun setUserNutritionGoals(goals: Map<String, Int>) {
        TODO("Not yet implemented")
    }

    override fun getUsername(): String {
        TODO("Not yet implemented")
    }

    override fun getPassword(): String {
        TODO("Not yet implemented")
    }

    override fun getUserName(): String {
        TODO("Not yet implemented")
    }

    override fun getUserAge(): Int {
        TODO("Not yet implemented")
    }

    override fun getUserHeight(): Int {
        TODO("Not yet implemented")
    }

    override fun getUserWeight(): Int {
        TODO("Not yet implemented")
    }

    override fun getUserFitnessGoal(): String {
        TODO("Not yet implemented")
    }

    fun isLoggedIn(): Boolean {
        return currentUsername.isNotEmpty()
    }
}


class FakeProfileViewModel : IProfileViewModel {
    private val _profile = MutableLiveData<Result<User>>()
    override val profile: LiveData<Result<User>> = _profile

    private val _updateResult = MutableLiveData<Result<User>?>()
    override val updateResult: LiveData<Result<User>?> = _updateResult

    // For testing
    var fetchedUsername: String? = null
    var updatedUsername: String? = null
    var updateRequest: UpdateProfileRequest? = null

    fun setProfile(result: Result<User>) {
        _profile.value = result
    }

    override fun fetchByUsername(username: String): Job {
        // Record the username for test verification
        fetchedUsername = username

        // Return a completed job for testing
        return Job().apply { complete() }
    }

    override fun updateProfile(username: String, req: UpdateProfileRequest): Job {

        updatedUsername = username
        updateRequest = req

        if (req.nutrition_goals != null) {
            val currentProfile = (_profile.value as? Result.Success<User>)?.data
            if (currentProfile != null) {
                val updatedUser = currentProfile.copy(
                    nutrition_goals = req.nutrition_goals!!
                )
                val successResult = Result.Success(updatedUser)
                _profile.value = successResult
                _updateResult.value = successResult
            }
        }
        // return a completed job for testing
        return Job().apply { complete() }
    }

    override fun clearUpdateState() {
        _updateResult.value = null
    }
}