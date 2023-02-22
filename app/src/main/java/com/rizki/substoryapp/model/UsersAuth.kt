package com.rizki.substoryapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UsersAuth(
    val Token: String,
    val IsLogin: Boolean
) : Parcelable
