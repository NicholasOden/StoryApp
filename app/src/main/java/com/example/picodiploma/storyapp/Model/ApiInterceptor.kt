package com.example.picodiploma.storyapp.Model

import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor(private val token: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (token != null) {
            request = request.newBuilder()
                .addHeader("Authorization","Bearer $token")
                .build()
        }

        return chain.proceed(request)
    }
}

