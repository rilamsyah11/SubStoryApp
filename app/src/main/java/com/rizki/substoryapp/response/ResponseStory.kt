package com.rizki.substoryapp.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class ResponseStory(

    @field:SerializedName("listStory")
    val ListStory: List<ListStory>,

    @field:SerializedName("error")
    val Error: Boolean,

    @field:SerializedName("message")
    val Message: String
)

@Entity(tableName = "stories")
data class ListStory(

    @PrimaryKey
    @field:SerializedName("id")
    val Id: String,

    @field:SerializedName("photoUrl")
    val PhotoUrl: String,

    @field:SerializedName("createdAt")
    val CreatedAt: String,

    @field:SerializedName("name")
    val Name: String,

    @field:SerializedName("description")
    val Description: String,

    @field:SerializedName("lon")
    val Lon: Double,

    @field:SerializedName("lat")
    val Lat: Double
)
