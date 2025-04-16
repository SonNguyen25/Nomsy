package nom.nom.nomsy.data.remote.network

import nom.nom.nomsy.data.remote.RecipeAPIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RecipeRetrofitInstance {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    val api: RecipeAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecipeAPIService::class.java)
    }
}
