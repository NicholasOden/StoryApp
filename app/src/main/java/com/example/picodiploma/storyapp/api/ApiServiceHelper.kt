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

    fun createAddStoryRequest(token: String, name: String, description: String, imageFile: File, lat: Float?, lon: Float?): Request {
        val requestBody = getAddStoryRequestBody(name, description, imageFile, lat, lon)
        return Request.Builder()
            .url(ApiServiceHelper.API_ENDPOINT + "/stories")
            .post(requestBody)
            .header("Authorization", "Bearer $token")
            .build()
    }

    private fun getAddStoryRequestBody(name: String, description: String, imageFile: File, lat: Float?, lon: Float?): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("name", name)
            .addFormDataPart("description", description)
            .addFormDataPart(
                "photo",
                imageFile.name,
                imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            )
            .apply {
                lat?.let { addFormDataPart("lat", it.toString()) }
                lon?.let { addFormDataPart("lon", it.toString()) }
            }
            .build()
    }

    suspend fun uploadStory(name: String, description: String, imageFile: File, lat: Float?, lon: Float?): AddNewStoryResponse {
        val request = createAddStoryRequest(token ?: "", name, description, imageFile, lat, lon)
        val response = withContext(Dispatchers.IO) {
            OkHttpClient().newCall(request).execute()
        }
        if (!response.isSuccessful) {
            throw Exception("Failed to upload story")
        }
        val responseBody = response.body?.string()
        return Gson().fromJson(responseBody, AddNewStoryResponse::class.java)
    }
}


