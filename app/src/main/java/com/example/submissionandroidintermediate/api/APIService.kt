package com.example.submissionandroidintermediate.api

import com.example.submissionandroidintermediate.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface APIService {
//    @Headers("Content-Type: application/json")
    @POST("register")
    fun createUser(@Body requestRegister: RegisterAccount): Call<ResponseDetail>

    @POST("login")
    fun fetchUser(@Body requestLogin: LoginAccount): Call<ResponseLogin>

    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String
    ): Call<ResponseStory>

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") token: String
    ): Call<ResponseDetail>
}