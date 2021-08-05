package com.example.mymoovingpicturedagger

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymoovingpicturedagger.domain.SendRouteDomain
import com.example.mymoovingpicturedagger.repozitory.Repozitory
import kotlinx.coroutines.launch
import javax.inject.Inject

class FragmentArchiveListViewModel@Inject constructor(private val coordddrepo: Repozitory): ViewModel() {
    init {
        viewModelScope.launch {
            coordddrepo.refreshServerRoutes()
        }
    }
var listDomainArchiveRoutes: LiveData<List<SendRouteDomain>> = coordddrepo.sentroutelistdomain

    suspend fun deleteRouteFromServerVM(recordNumber:Long){
        coordddrepo.deleteRouteFromServer(recordNumber)
    }
    suspend fun refreshServerRoutesVM(){
        coordddrepo.refreshServerRoutes()
    }
    suspend fun downloadRouteFromServerVM(recordNumber :Long){
        coordddrepo.downloadRouteFromServer(recordNumber)
    }
}