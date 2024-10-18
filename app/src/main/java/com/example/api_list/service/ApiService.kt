package com.example.api_list.service

import com.example.api_list.model.Item
import com.example.api_list.model.ItemValue
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("items")
    suspend fun getItems(): List<Item>

    @GET("items/{id}")
    suspend fun getItem(@Path("id") id: String): Item

    @DELETE("items/{id}")
    suspend fun deleteItem(@Path("id") id: String)

    @POST("items")
    suspend fun createItem(@Body item: ItemValue): Item

    @PATCH("items/{id}")
    suspend fun patchItem(@Path("id") id: String, @Body item: ItemValue): Item

}