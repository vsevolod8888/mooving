package com.example.mymoovingpicture

import androidx.lifecycle.*
import com.example.mymoovingpicturedagger.database.CoordinatesEntity
import com.example.mymoovingpicturedagger.database.RouteEntity
import com.example.mymoovingpicturedagger.repozitory.Repozitory
import com.example.mymoovingpicturedagger.domain.CoordinatesDomain
import javax.inject.Inject


class MapDrawingViewModel @Inject constructor(private val coordddrepo: Repozitory) : ViewModel() {
    private var numbOfRecord: Long? = null

    fun getCoordByRecordNumber(recordNumber: Long): LiveData<List<CoordinatesDomain>> {
        return coordddrepo.getCoordinatesByRecordNumber(recordNumber)
    }

    fun getSharedPreferencesRouteNameVM(): String? {
        return coordddrepo.getSharedPreferencesRouteName()
    }

    suspend fun lastNumberOfList(): Long {
        if (numbOfRecord != null) {
            return numbOfRecord!!
        }
        var lastNumberOfList = coordddrepo.lastNumberOfList()
        numbOfRecord = if (lastNumberOfList == null) {
            System.currentTimeMillis()
        } else {
            System.currentTimeMillis()
        }
        return numbOfRecord!!
    }

    suspend fun saveDrawCoord(
        lat: Double,
        lon: Double,
        t: Long,
        recNum: Long
    ) {
        val newcoord = CoordinatesEntity(
            id = 0,
            checkTime = t,
            recordNumber = recNum,                                                              //recordRouteName = nameOfRoute,
            Lattitude = lat,
            Longittude = lon
        )
        coordddrepo.insertCoord(newcoord)
    }

    suspend fun saveDrawRoute(nameOfDrRoute: String, numbOfRecord: Long) {
        val newRoute =
            RouteEntity(
                id = numbOfRecord!!,
                checkTime = System.currentTimeMillis(),
                recordRouteName = nameOfDrRoute,
                isClicked = false
            )
        coordddrepo.insertRoute(newRoute)
    }
    suspend fun getCoordinatesByRecordNumberSusVM(kk: Long): List<CoordinatesDomain> {
        return coordddrepo.getCoordinatesByRecordNumberSus(kk)
    }
}