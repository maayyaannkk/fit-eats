package com.fiteats.app.models

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("age")
    val age: String?= null,

    @SerializedName("sex")
    val sex: String?= null,

    @SerializedName("country")
    val country: String?= null
)