package com.example.api_list.service

import com.example.api_list.database.dao.UserLocationDAO
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class GeoLocationInterceptor(private val userLocationDAO: UserLocationDAO): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val userLastLocaltion = runBlocking {
            userLocationDAO.getLastUserLocation()
        }

        val originalRequest: Request = chain.request()
        val newRequest = userLastLocaltion?.let{
            originalRequest.newBuilder()
                .addHeader("x-data-latitude", userLastLocaltion.latitude.toString())
                .addHeader("x-data-longitude", userLastLocaltion.longitude.toString())
                .build()

        } ?: originalRequest

        return chain.proceed(newRequest)
    }
}
