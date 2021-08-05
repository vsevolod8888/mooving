package com.example.mymoovingpicturedagger

import androidx.lifecycle.ViewModel
import com.example.mymoovingpicturedagger.repozitory.Repozitory
import javax.inject.Inject

class EnterRouteNameViewModel@Inject constructor(private val coordddrepo: Repozitory):ViewModel() {

    fun getSharedPreferencesTimeRepeatVM():Int?{
        return coordddrepo.getSharedPreferencesTimeRepeat()
    }
    fun putSharedPreferencesTimeRepeatVM(pref:Int){
        coordddrepo.putSharedPreferencesTimeRepeat(pref)
    }
}