package com.example.mymoovingpicturedagger.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.mymoovingpicturedagger.domain.CoordinatesDomain
import com.example.mymoovingpicturedagger.domain.RouteDomain

@Entity(tableName = "route")
data class RouteEntity(                        //WeatherDatabase
    @PrimaryKey//(autoGenerate = true)                                //(autoGenerate = true)
    var id: Long,//= System.currentTimeMillis()
    @ColumnInfo(name = "checktime")
    var checkTime: Long,//= System.currentTimeMillis()
    @ColumnInfo(name = "recordRouteName")
    var recordRouteName: String,
    @ColumnInfo(name = "isClicked")
    var isClicked: Boolean
)// добавить поле булеан загружено

fun List<RouteEntity>.asDomainRouteModel(): List<RouteDomain> { // функция для преобразования DatabaseVideo объектов базы данных в объекты домена
    return map {
        val routeDomain = RouteDomain(
            id = it.id,
            time = it.checkTime,
            recordRouteName = it.recordRouteName,
            isClicked = it.isClicked
        )
        routeDomain
    }
}

fun RouteEntity.asDomainRouteModel(): RouteDomain { // функция для преобразования Database объектов базы данных в объекты домена
    return RouteDomain(
        id = this.id,
        time = this.checkTime,
        recordRouteName = this.recordRouteName,
        isClicked = this.isClicked
    )
}


@Entity(tableName = "coord")
data class CoordinatesEntity(                        //WeatherDatabase
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "checktime")
    var checkTime: Long,
    @ColumnInfo(name = "recordNumber")
    var recordNumber: Long,                        // было Int
    @ColumnInfo(name = "lattitude")
    var Lattitude: Double,
    @ColumnInfo(name = "longittude")
    var Longittude: Double
) {
    constructor() : this(0, 0, 0, 0.0, 0.0)
}

fun List<CoordinatesEntity>.asDomainCoordinatesModel(): List<CoordinatesDomain> { // функция для преобразования DatabaseVideo объектов базы данных в объекты домена
    return map {
        CoordinatesDomain(
            id = it.id,
            recordNumber = it.recordNumber,
            time = it.checkTime,
            lattitude = it.Lattitude,
            longittude = it.Longittude
        )
    }
}