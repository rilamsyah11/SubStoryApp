package com.rizki.substoryapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stories(
    var Name: String? = null,
    var Photo: String? = null,
    var Description: String? = null,
    var Lat: Double? = null,
    var Lon: Double? = null
) : Parcelable
