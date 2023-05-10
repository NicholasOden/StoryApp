package com.example.picodiploma.storyapp.api

import com.example.picodiploma.storyapp.api.Response.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ApiServiceHelper(private val token: String?) {

    private val apiService: ApiService

    init {
        val apiConfig = ApiConfig()
        apiService = apiConfig.getApiService()
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

    suspend fun uploadStory(description: RequestBody, imageFile: File, lat: RequestBody? = null, lon: RequestBody? = null): AddNewStoryResponse {
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
