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

    @GET("stories")
    fun getStory2(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): ResponseStory

    @Multipart
    @POST("stories")
    fun uploadPicture(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") token: String
    ): Call<ResponseDetail>
}