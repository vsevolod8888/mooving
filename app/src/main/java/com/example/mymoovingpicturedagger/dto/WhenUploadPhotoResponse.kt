package com.example.mymoovingpicturedagger.dto

import com.google.gson.annotations.SerializedName

data class WhenUploadPhotoResponse(
    @SerializedName("recordNumber") var id: Int?,
    @SerializedName("recordNumber") var username: String?,
    @SerializedName("recordNumber") val email: String?,
    @SerializedName("recordNumber") var photo: String?
)
