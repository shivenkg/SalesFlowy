package com.example.data.remote.mongodb

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface MongoDbApiService {

    @Headers(
        "Content-Type: application/json",
        "Access-Control-Request-Headers: *"
    )
    @POST("action/insertOne")
    suspend fun insertOne(
        @Header("api-key") apiKey: String,
        @Body body: Any
    ): Response<ResponseBody>

    @Headers(
        "Content-Type: application/json",
        "Access-Control-Request-Headers: *"
    )
    @POST("action/insertMany")
    suspend fun insertMany(
        @Header("api-key") apiKey: String,
        @Body body: Any
    ): Response<ResponseBody>

    @Headers(
        "Content-Type: application/json",
        "Access-Control-Request-Headers: *"
    )
    @POST("action/find")
    suspend fun find(
        @Header("api-key") apiKey: String,
        @Body body: MongoFindRequest
    ): Response<ResponseBody>
}
