package com.example.mymoovingpicturedagger.dto

import android.annotation.SuppressLint
import com.example.mymoovingpicturedagger.database.CoordinatesEntity
import com.example.mymoovingpicturedagger.database.RouteEntity
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class SendRouteRequest(
    @SerializedName("recordNumber") val recordNumber: Long,
    @SerializedName("recordName") val recordName: String,
    @SerializedName("coordinates") val coords: List<CoordRequest>
)
@SuppressLint("SimpleDateFormat")
fun reverseConvertTimeStringToLong(s:String):Long{
    val myDate = s
    val sdf = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
    val date: Date? = sdf.parse(myDate)
    val millis: Long = date!!.getTime()
    return millis
}

fun convertSentRouteToRouteEntity(r:SendRouteRequest): RouteEntity {
    val routeEntity= RouteEntity (id =r.recordNumber,
        checkTime = reverseConvertTimeStringToLong(r.coords[0].createAt) ,recordRouteName = r.recordName,isClicked = false)//
    return routeEntity
}



fun convertCoordDataToCoordinatesEntity(r: SendRouteRequest):List<CoordinatesEntity>{
    val coordlist = mutableListOf<CoordinatesEntity>()

    r.coords.forEach {
        coordlist.add(
            CoordinatesEntity(
                id = it.id.toInt(),
                checkTime = reverseConvertTimeStringToLong(it.createAt) ,//
                recordNumber = r.recordNumber,
                Lattitude = it.lat,
                Longittude = it.lng
            )
        )
    }
    return coordlist
}