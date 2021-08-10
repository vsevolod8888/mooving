package com.example.mymoovingpicture

import android.app.Application
import androidx.lifecycle.*
import com.example.mymoovingpicturedagger.database.getDatabase
import com.example.mymoovingpicturedagger.domain.CoordinatesDomain
import com.example.mymoovingpicturedagger.domain.RouteDomain
import com.example.mymoovingpicturedagger.repozitory.Repozitory
import com.example.mymoovingpicturedagger.database.getDatabase

import kotlinx.coroutines.launch
import javax.inject.Inject


class MapChosenRouteViewModel @Inject constructor(private val coordddrepo: Repozitory) :
    ViewModel() {

    fun getCoordinatesBeId(id: Long): LiveData<List<CoordinatesDomain>> {
        return coordddrepo.getCoordinatesByRecordNumber(id)
    }

    suspend fun getRouteById(id: Long): RouteDomain? {
        return coordddrepo.getRouteById(id)
    }
}