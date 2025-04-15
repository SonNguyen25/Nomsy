//package com.example.nomsy
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.Observer
//import androidx.room.DatabaseConfiguration
//import androidx.room.InvalidationTracker
//import androidx.room.RoomDatabase
//import androidx.sqlite.db.SupportSQLiteDatabase
//import androidx.sqlite.db.SupportSQLiteOpenHelper
//import com.example.nomsy.data.local.UserDatabase
//import com.example.nomsy.data.local.dao.UserDao
//import com.example.nomsy.data.local.models.User
//import com.example.nomsy.data.remote.*
//import com.example.nomsy.data.repository.AuthRepository
//import com.example.nomsy.utils.Result
//import kotlinx.coroutines.runBlocking
//import okhttp3.MediaType
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.ResponseBody
//import org.junit.*
//import org.junit.Assert.*
//import org.mockito.ArgumentMatchers.any
//import retrofit2.Response
//
//// --- helper to await LiveData values synchronously ---
//fun <T> LiveData<T>.getOrAwaitValue(timeout: Long = 2_000L): T {
//    var data: T? = null
//    val latch = java.util.concurrent.CountDownLatch(1)
//    val obs = object : Observer<T> {
//        override fun onChanged(t: T) {
//            data = t
//            latch.countDown()
//            this@getOrAwaitValue.removeObserver(this)
//        }
//    }
//    this.observeForever(obs)
//    if (!latch.await(timeout, java.util.concurrent.TimeUnit.MILLISECONDS)) {
//        throw java.util.concurrent.TimeoutException("LiveData value was never set.")
//    }
//    @Suppress("UNCHECKED_CAST")
//    return data as T
//}
//
//// --- a simple fake AuthApiService ---
//open class FakeAuthApi : AuthApiService {
//    var loginResponse: Response<LoginResponse>? = null
//    override suspend fun login(request: LoginRequest) = loginResponse!!
//
//    var registerResponse: Response<RegisterResponse>? = null
//    override suspend fun register(request: RegisterRequest) = registerResponse!!
//
//    var getProfileResponse: Response<GetProfileResponse>? = null
//    override suspend fun getProfile(userId: String) = getProfileResponse!!
//
//    var getUserByUsernameResponse: Response<GetProfileResponse>? = null
//    override suspend fun getUserByUsername(username: String) = getUserByUsernameResponse!!
//
//    var updateProfileResponse: Response<GetProfileResponse>? = null
//    override suspend fun updateProfile(username: String, request: UpdateProfileRequest) =
//        updateProfileResponse!!
//}
//
//class FakeUserDao : UserDao {
//    private val byId = mutableMapOf<String, User>()
//
//    override suspend fun insertUser(user: User) {
//        byId[user.id] = user
//    }
//
//    override suspend fun getUserById(userId: String): User? {
//        return byId[userId]
//    }
//
//    override suspend fun getUserByUsername(username: String): User? {
//        return byId.values.firstOrNull { it.username == username }
//    }
//}
//
//abstract class FakeUserDatabase(private val dao: UserDao) : UserDatabase() {
//    override fun userDao(): UserDao = dao
//    override fun clearAllTables() {
//        return
//    }
//}
//
//class AuthRepositoryTest {
//    @get:Rule
//    val instantExecutorRule = InstantTaskExecutorRule()
//
//    private lateinit var fakeApi: FakeAuthApi
//    private lateinit var fakeDao: FakeUserDao
//    private lateinit var fakeDb: FakeUserDatabase
//    private lateinit var repo: AuthRepository
//
//    @Before fun setup() {
//        fakeApi = FakeAuthApi()
//        fakeDao = FakeUserDao()
//        fakeDb  = FakeUserDatabase(fakeDao)
//        repo    = AuthRepository(authApi = fakeApi, userDatabase = fakeDb)
//    }
//
//    @Test
//    fun login_success_emitsSuccessAndPersistsUser() = runBlocking {
//        // arrange
//        val u = User(
//            id = "id1",
//            username = "bob",
//            password = "pw",
//            name = "Bob",
//            age = 30,
//            height = 180,
//            weight = 75,
//            fitness_goal = "bulk",
//            nutrition_goals = mapOf(
//                "calories" to 2000,
//                "protein" to 100,
//                "carbs" to 250,
//                "fat" to 70,
//                "water" to 3
//            )
//        )
//        fakeApi.loginResponse = Response.success(LoginResponse("ok", u))
//
//        // act
//        val result = repo.login("bob", "pw").getOrAwaitValue()
//
//        // assert
//        assertTrue(result is Result.Success)
//        assertEquals(u, (result as Result.Success).data)
//
//        // verify persistence
//        val saved = runBlocking { fakeDao.getUserById("id1") }
//        assertEquals(u, saved)
//    }
//
//    @Test
//    fun login_httpError_emitsError() {
//        // arrange: HTTP 401
//        fakeApi.loginResponse = Response.error(
//            401,
//            ResponseBody.create("text/plain".toMediaTypeOrNull(), ""))
//        // act
//        val result = repo.login("bob", "wrong").getOrAwaitValue()
//        // assert
//        assertTrue(result is Result.Error)
//        assertTrue((result as Result.Error).exception.message!!.contains("status code"))
//    }
//
//    @Test
//    fun getProfileByUsername_networkFails_fallsBackToCache() = runBlocking {
//        // seed cache
//        val cached = User(
//            id = "id2",
//            username = "alice",
//            password = "pw",
//            name = "Alice",
//            age = 28,
//            height = 165,
//            weight = 60,
//            fitness_goal = "maintain",
//            nutrition_goals = mapOf(
//                "calories" to 1800,
//                "protein" to 80,
//                "carbs" to 200,
//                "fat" to 60,
//                "water" to 2
//            )
//        )
//        fakeDao.insertUser(cached)
//
//        // make API throw
//        val brokenApi = object : FakeAuthApi() {
//            override suspend fun getUserByUsername(username: String): Response<GetProfileResponse> {
//                throw Exception("network down")
//            }
//        }
//        repo = AuthRepository(authApi = brokenApi, userDatabase = fakeDb)
//
//        // act
//        val result = repo.getProfileByUsername("alice").getOrAwaitValue()
//
//        // assert fallback
//        assertTrue(result is Result.Success)
//        assertEquals(cached, (result as Result.Success).data)
//    }
//}