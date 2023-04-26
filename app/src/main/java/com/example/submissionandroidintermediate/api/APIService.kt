package com.example.submissionandroidintermediate.api

import com.example.submissionandroidintermediate.*
import com.example.submissionandroidintermediate.dataclass.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface APIService {
    @POST("register")
    fun registUser(@Body requestRegister: RegisterDataAccount): Call<ResponseDetail>

    @POST("login")
    fun loginUser(@Body requestLogin: LoginDataAccount): Call<ResponseLogin>

    @GET("stories")
    fun getStory(
        @Header("Authorization") token: String,
    ): Call<ResponseStory>

    @Multipart
    @POST("stories")
    fun uploadPicture(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
        @Header("Authorization") token: String
    ): Call<ResponseDetail>
}