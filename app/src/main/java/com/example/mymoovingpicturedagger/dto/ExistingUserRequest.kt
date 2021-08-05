package com.example.mymoovingpicturedagger.dto

import com.google.gson.annotations.SerializedName

data class ExistingUserRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)
