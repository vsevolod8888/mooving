package com.example.mymoovingpicturedagger

import androidx.lifecycle.ViewModel
import com.example.mymoovingpicturedagger.repozitory.Repozitory
import javax.inject.Inject

class FragmentNameDrawingRootViewModel@Inject constructor(private val coordddrepo: Repozitory):ViewModel() {

    fun putSharedPreferencesRouteNameVM(pref:String){
        coordddrepo.putSharedPreferencesRouteName(pref)
    }


}