package com.rizki.substoryapp.response

import com.google.gson.annotations.SerializedName

data class ResponseRegister(

    @field:SerializedName("error")
    val Error: Boolean,

    @field:SerializedName("message")
    val Message: String
)
