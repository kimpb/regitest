package com.example.ayu.regitest.Firebase

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserDTO(
        var uId : String? = null,
        var userId : String? = null,
        var token : String? = null
) : Parcelable