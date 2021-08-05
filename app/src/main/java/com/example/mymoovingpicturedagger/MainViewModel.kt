package com.example.mymoovingpicturedagger

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymoovingpicturedagger.dto.CheckAmIEnterResponse
import com.example.mymoovingpicturedagger.repozitory.Repozitory
import kotlinx.coroutines.launch
import javax.inject.Inject
class MainViewModel@Inject constructor(private val coordddrepo: Repozitory): ViewModel() {

    init {
        viewModelScope.launch {
            coordddrepo.checkIfUserIsEnter()
        }
    }
    suspend fun checkIsUserEnterVM(){
        coordddrepo.checkIfUserIsEnter()
    }

    var enterUserNow: LiveData<CheckAmIEnterResponse?> = coordddrepo.enterUser

    suspend fun uploadPhotoOnServerVM(uri: Uri){
        coordddrepo.uploadPhotoOnServer(uri)
    }
    suspend fun checkIfUserEnterVM(){
        coordddrepo.checkIfUserIsEnter()
    }
    suspend fun deleteListAllRoutes(){
        coordddrepo.deleteList()
    }
    suspend fun isEmpty():Boolean{
       return coordddrepo.isEmptyy()
    }
     suspend fun toMakeTokenNull(){
        coordddrepo.makeTokenNull()
    }
    fun getToken():String?{
        return  coordddrepo.getToken()
    }
}