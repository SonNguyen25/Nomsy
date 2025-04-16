package nom.nom.nomsy.data.remote

import android.graphics.Bitmap
import android.util.Log
import nom.nom.nomsy.data.local.entities.Food
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream

object SpoonacularApiService {
    private const val API_KEY = "b1841f15dcc444d99a6bb5a44cb1351b"

    suspend fun analyzeFoodImage(bitmap: Bitmap): Food? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "image.jpg", byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull()))
            .addFormDataPart("apiKey", API_KEY)
            .build()

        val request = Request.Builder()
            .url("https://api.spoonacular.com/food/images/analyze")
            .addHeader("x-api-key", API_KEY)
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        return try {
            val response = client.newCall(request).execute()

            Log.d("Client Response", response.toString())

            if (!response.isSuccessful) {
                Log.e("Spoonacular", "Request failed: ${response.code}")
                Log.e("Spoonacular", "Raw body: ${response.body?.string()}")
                return null
            }

            val bodyString = response.body?.string() ?: return null
            val json = JSONObject(bodyString)

            if (!json.has("category") || !json.has("nutrition")) {
                Log.e("Spoonacular", "Missing category or nutrition: $bodyString")
                return null
            }

            val category = json.getJSONObject("category").optString("name", "Unknown")

            val nutrition = json.getJSONObject("nutrition")
            val calories = nutrition.getJSONObject("calories").getDouble("value").toInt()
            val protein = nutrition.getJSONObject("protein").getDouble("value").toInt()
            val carbs = nutrition.getJSONObject("carbs").getDouble("value").toInt()
            val fat = nutrition.getJSONObject("fat").getDouble("value").toInt()

            Food(
                id = "",
                date = "",
                meal_type = "",
                food_name = category,
                calories = calories,
                protein = protein,
                carbs = carbs,
                fat = fat
            )

        } catch (e: Exception) {
            Log.e("Spoonacular", "Analyze Error: ${e.message}", e)
            null
        }
    }



}
