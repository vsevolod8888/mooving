package com.example.mymoovingpicturedagger.dto

import com.google.gson.annotations.SerializedName

data class FromServerLogInResponse(
    @SerializedName("token") val token: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("id") val id: Int?,
    @SerializedName("username") val username: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("roles") val roles: String?
)
