package com.example.mymoovingpicture

import android.app.Application
import androidx.lifecycle.*
import com.example.mymoovingpicturedagger.database.getDatabase
import com.example.mymoovingpicturedagger.domain.CoordinatesDomain
import com.example.mymoovingpicturedagger.domain.RouteDomain
import com.example.mymoovingpicturedagger.repozitory.Repozitory
import kotlinx.coroutines.launch
import javax.inject.Inject


class MapNewRouteViewModel@Inject constructor(private val coordddrepo: Repozitory): ViewModel() {
    private var numbOfRecord:Long?= null
    fun getCoordByRecordNumber(recordNumber:Long):LiveData<List<CoordinatesDomain>>{
        return coordddrepo.getCoordinatesByRecordNumber(recordNumber)
    }

    suspend fun lastNumberOfList():Long{
        if(numbOfRecord!=null){
            return numbOfRecord!!
        }
        var lastNumberOfList = coordddrepo.lastNumberOfList()
        numbOfRecord =if(lastNumberOfList == null){
            0
        }else{
            lastNumberOfList
        }
        return numbOfRecord!!
    }
}