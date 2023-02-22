package com.rizki.substoryapp.response

import com.google.gson.annotations.SerializedName

data class ResponseLogin(

    @field:SerializedName("loginResult")
    val ResultLogin: ResultLogin,

    @field:SerializedName("error")
    val Error: Boolean,

    @field:SerializedName("message")
    val Message: String
)

data class ResultLogin(

    @field:SerializedName("name")
    val Name: String,

    @field:SerializedName("userId")
    val UserId: String,

    @field:SerializedName("token")
    val Token: String
)