package com.example.mymoovingpicturedagger.domain

import com.google.gson.annotations.SerializedName

data class RouteDomain(
    val id: Long,
    val time: Long,
    val recordRouteName: String,
    var isClicked: Boolean
)



