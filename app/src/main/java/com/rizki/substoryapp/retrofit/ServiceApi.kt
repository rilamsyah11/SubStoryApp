package com.rizki.substoryapp.retrofit

import com.rizki.substoryapp.response.ResponseLogin
import com.rizki.substoryapp.response.ResponseRegister
import com.rizki.substoryapp.response.ResponseStory
import com.rizki.substoryapp.response.ResponseUploadFile
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ServiceApi {
    @FormUrlEncoded
    @POST("register")
    fun registerUser(
        @Field("name") Name: String,
        @Field("email") Email: String,
        @Field("password") Password: String
    ) : Call<ResponseRegister>

    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") Email: String,
        @Field("password") Password: String
    ) : Call<ResponseLogin>

    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") Header: String,
        @Query("page") Page: Int,
        @Query("size") Size: Int
    ) : ResponseStory

    @Multipart
    @POST("stories")
    fun uploadFile(
        @Header("Authorization") Header: String,
        @Part file: MultipartBody.Part,
        @Part("description") Description: RequestBody,
    ): Call<ResponseUploadFile>

    @GET("stories")
    fun getStoryWithLocation(
        @Header("Authorization") Header: String,
        @Query("page") Page: Int,
        @Query("size") Size: Int,
        @Query("location") Location: Int
    ) : Call<ResponseStory>


}