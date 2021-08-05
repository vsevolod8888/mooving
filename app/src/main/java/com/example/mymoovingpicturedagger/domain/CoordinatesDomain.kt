package com.example.mymoovingpicturedagger.domain

data class CoordinatesDomain(
    val id: Int,
    val recordNumber: Long, // было Int
    val time: Long,
    val lattitude: Double,
    val longittude: Double
)
