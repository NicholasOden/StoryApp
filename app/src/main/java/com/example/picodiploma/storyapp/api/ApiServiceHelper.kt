package com.example.picodiploma.storyapp.api

import com.example.picodiploma.storyapp.api.Response.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class ApiServiceHelper(private val token: String?) {

    private val apiService: ApiService

    companion object {
        const val API_ENDPOINT = "https://story-api.dicoding.dev/v1/"
    }

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(ApiInterceptor(token))
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    fun getService(): ApiService = apiService

    suspend fun login(email: String, password: String): LoginResponse {
        val loginRequest = LoginRequest(email, password)
        val response = apiService.loginUser(loginRequest)
        return response.body() ?: LoginResponse(error = true, message = "Unknown error")
    }

    suspend fun registerUser(userRegistration: UserRegistration): RegisterResponse {
        return apiService.registerUser(userRegistration)
    }

    suspend fun getStoryList(page: Int = 0, size: Int = 10, location: Int = 0): List<Story> {
        val authorization = "Bearer $token"
        val response = apiService.getStoryList(authorization, page, size, location)
        if (response.isSuccessful) {
            return response.body()?.listStory ?: emptyList()
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to get story list: $errorBody")
        }
    }

    suspend fun getStoryDetail(id: String): DetailResponse {
        val authorization = "Bearer $token"
        val response = apiService.getStoryDetail(authorization, id)
        return response.body() ?: DetailResponse(error = true, message = "Unknown error", data = null)
    }

    /*fun uploadStoryToServer(description: String, imageFile: File, lat: Float?, lon: Float?): AddNewStoryResponse {
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
        val filePart = MultipartBody.Part.createFormData("photo", imageFile.name, requestBody)
        val descriptionPart = RequestBody.create("text/plain".toMediaTypeOrNull(), description)
        val latPart =
            lat?.let { RequestBody.create("text/plain".toMediaTypeOrNull(), it.toString()) }
        val lonPart =
            lon?.let { RequestBody.create("text/plain".toMediaTypeOrNull(), it.toString()) }

        return try {
            val response =
                apiService.uploadStory("Bearer $token", descriptionPart, filePart, latPart, lonPart)
            response
        } catch (e: Exception) {
            throw Exception("Failed to upload story", e)
        }
    }*/
     */

    fun uploadStory(description: RequestBody, imageFile: File, lat: RequestBody? = null, lon: RequestBody? = null): AddNewStoryResponse {
        val authorization = "Bearer $token"
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        return apiService.uploadStory(authorization, description, imageMultipart, lat, lon)
    }



}


