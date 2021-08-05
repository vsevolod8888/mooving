package com.example.mymoovingpicturedagger.dto

import com.google.gson.annotations.SerializedName

data class RegistrationNewUserRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
