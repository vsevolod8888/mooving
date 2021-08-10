package com.example.mymoovingpicturedagger

import androidx.lifecycle.*
import com.example.mymoovingpicturedagger.domain.CoordinatesDomain
import com.example.mymoovingpicturedagger.repozitory.Repozitory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class MapAllViewModel @Inject constructor(private val coordddrepo: Repozitory) : ViewModel() {
    suspend fun getCoordinatesBeIdSuspend(id: Long): List<CoordinatesDomain> {
        return coordddrepo.getCoordinatesByRecordNumberSus(id)
    }

    suspend fun getOnlyIdListVM(): List<Long> {
        return coordddrepo.getOnlyIdList()
    }

    suspend fun getInfoById(routeNumber: Long): String {
        var route = coordddrepo.getRouteById(routeNumber)
        var name = route!!.recordRouteName
        var time = route.time
        val locale = Locale("ru", "RU")
        val format2: DateFormat = SimpleDateFormat(("dd MM yy, HH:mm"), locale)
        val date: String = format2.format(time).capitalize() //"   "
        val description = "$name " + ", " + date
        return description
    }
}