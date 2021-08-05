package com.example.mymoovingpicture


import androidx.lifecycle.*
import com.example.mymoovingpicturedagger.domain.CoordinatesDomain
import com.example.mymoovingpicturedagger.domain.RouteDomain
import com.example.mymoovingpicturedagger.dto.CoordRequest
import com.example.mymoovingpicturedagger.dto.SendRouteRequest
import com.example.mymoovingpicturedagger.repozitory.Repozitory
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class FragmentCoordListViewModel @Inject constructor(private val coordddrepo: Repozitory
): ViewModel() {
private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 64, onBufferOverflow = BufferOverflow.DROP_OLDEST)
   val event = _events.asSharedFlow()
    val routeList = coordddrepo.routeslist
    lateinit var lastCoordinatesList: LiveData<List<CoordinatesDomain>>                          // сколько записей у нас есть инт
    init {
        viewModelScope.launch {
            setup()
        }
    }
    suspend fun setup(){
        val lastRoute = coordddrepo.lastNumberOfList() ?: 0
        lastCoordinatesList = coordddrepo.getCoordinatesByRecordNumber(lastRoute)
    }
    fun getCoordinatesBeId(id:Long):LiveData<List<CoordinatesDomain>>{
        return coordddrepo.getCoordinatesByRecordNumber(id)
    }
    private val _choosenroute = MutableLiveData<RouteDomain>()
    val choosenroute: LiveData<RouteDomain>
        get() = _choosenroute

    fun onClickDetail(choosenf: RouteDomain) {
        _choosenroute.value = choosenf
    }
     fun uploadRouteOnServer(itemDetail: RouteDomain){
         viewModelScope.launch {
             val myCoords = coordddrepo.getCoordinatesByRecordNumberSus(itemDetail.id)
             var rrr = coordddrepo.database.coorddao.getRouteById(itemDetail.id)
             val sendRoute = SendRouteRequest(
                 recordNumber = itemDetail.id.toLong(),
                 recordName = itemDetail.recordRouteName,
                 coords = coordDomainToMyCoords(myCoords)
             )
             if (rrr != null) {
                 coordddrepo.onUploadRouteOnSErver(rrr,sendRoute)
             }
         }
    }
    fun registration(){
        _events.tryEmit(Event.Registration)
    }


    fun coordDomainToMyCoords(mc: List<CoordinatesDomain>): List<CoordRequest> {
        val answerlist = mutableListOf<CoordRequest>()
        for (k in 0 until mc.size) {
            val coorrrddd: CoordRequest = CoordRequest(
                mc[k].id.toLong(),
                comvertTime(mc[k].time),
                mc[k].lattitude,
                mc[k].longittude
            )
            answerlist.add(k, coorrrddd)
        }
        return answerlist
    }
    fun comvertTime(l: Long): String {
        // val date = Date(l * 1000L)
        val locale = Locale("ru", "RU")
        val format2: DateFormat =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale)          //2021-06-23 16:20:39
        val daytoday: String = format2.format(l)          //.capitalize()
        return daytoday
    }
    suspend fun deleteRouteAndRecordNumberTogether(id:Long){
        coordddrepo.deleteRouteAndRecordNumberTogether(id)
    }

    sealed class Event{
        object Registration:Event()

    }

}