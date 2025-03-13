package com.codetech.texttoimage.abstraction

import com.codetech.texttoimage.abstraction.model.HuggingFaceRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface HuggingFaceApi {

    @POST("models/black-forest-labs/FLUX.1-dev")
    fun generateImage(
        @Header("Authorization") token: String,
        @Body request: HuggingFaceRequest
    ): Call<ResponseBody>

    @POST("models/openfree/president-pjh")
    fun generatePresidentImage(
        @Header("Authorization") token: String,
        @Body request: HuggingFaceRequest
    ): Call<ResponseBody>

    @POST("models/stabilityai/stable-diffusion-3.5-large")
    fun generateStableDiffusionImage(
        @Header("Authorization") token: String,
        @Body request: HuggingFaceRequest
    ): Call<ResponseBody>

}