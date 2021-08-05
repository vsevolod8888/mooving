package com.example.mymoovingpicturedagger.dto

import com.google.gson.annotations.SerializedName

data class CoordRequest(
    @SerializedName("id") var id: Long,
    @SerializedName("createdAt") var createAt: String,
    @SerializedName("lat") var lat: Double,
    @SerializedName("lng") var lng: Double
)