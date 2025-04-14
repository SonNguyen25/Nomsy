package com.example.nomsy.data.remote

import android.graphics.Bitmap
import android.util.Base64
import com.example.nomsy.data.local.models.Food
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

object SpoonacularApiService {
    private const val API_KEY = "5426cfe3532a41c18494f673dfc03870"

    fun guessNutrition(foodName: String, onResult: (Food?) -> Unit) {
        val url = "https://api.spoonacular.com/recipes/guessNutrition?title=${foodName}&apiKey=$API_KEY"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let {
                    try {
                        val json = JSONObject(it)
                        val food = Food(
                            id = "", // Generate or leave blank for now
                            date = "", // Fill in when saving
                            meal_type = "",
                            food_name = foodName,
                            calories = json.getJSONObject("calories").getDouble("value").toInt(),
                            carbs = json.getJSONObject("carbs").getDouble("value").toInt(),
                            protein = json.getJSONObject("protein").getDouble("value").toInt(),
                            fat = json.getJSONObject("fat").getDouble("value").toInt()
                        )
                        onResult(food)
                    } catch (e: Exception) {
                        onResult(null)
                    }
                } ?: onResult(null)
            }
        })
    }

}
