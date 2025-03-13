package com.codetech.texttoimage.abstraction

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.codetech.texttoimage.abstraction.model.HuggingFaceRequest
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class HuggingFaceImageGenerator {


    private val api: HuggingFaceApi
    private val API_KEY ="" // your access token
    private val TAG = "HuggingFaceImageGeneratorInfo"
    private val client = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val request = chain.request()
                var response: okhttp3.Response? = null
                var tryCount = 0
                val maxTries = 3

                while (tryCount < maxTries) {
                    try {
                        response = chain.proceed(request)
                        if (response.isSuccessful) {
                            return response
                        } else {
                            response.body?.close()
                        }
                    } catch (e: IOException) {
                        Log.d(TAG, "Request failed - retrying (${tryCount + 1}/$maxTries)")
                    }
                    tryCount++
                }

                return response ?: okhttp3.Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(500)
                    .message("Failed after $maxTries retries")
                    .body(ResponseBody.create(null, ""))
                    .build()
            }
        })
        .build()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api-inference.huggingface.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(HuggingFaceApi::class.java)
    }

    suspend fun generateImage(prompt: String): Bitmap? {
        return try {
            val request = HuggingFaceRequest(inputs = prompt)
            val response = api.generateImage("Bearer $API_KEY", request).awaitResponse()
            if (response.isSuccessful) {
                val imageBytes = response.body()?.bytes()
                imageBytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
            } else {
                Log.d(TAG, "onResponse:fail ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.d(TAG, "onFailure: ${e.message}")
            null
        }
    }

    suspend fun generatePresidentImage(prompt: String): Bitmap? {
        return try {
            val request = HuggingFaceRequest(inputs = prompt)
            val response = api.generatePresidentImage("Bearer $API_KEY", request).awaitResponse()
            if (response.isSuccessful) {
                val imageBytes = response.body()?.bytes()
                imageBytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
            } else {
                Log.d(TAG, "onResponse:fail ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.d(TAG, "onFailure: ${e.message}")
            null
        }
    }

    suspend fun generateStableDiffusionImage(prompt: String): Bitmap? {
        return try {
            val request = HuggingFaceRequest(inputs = prompt)
            val response = api.generateStableDiffusionImage("Bearer $API_KEY", request).awaitResponse()
            if (response.isSuccessful) {
                val imageBytes = response.body()?.bytes()
                imageBytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
            } else {
                Log.d(TAG, "onResponse:fail ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.d(TAG, "onFailure: ${e.message}")
            null
        }
    }
}