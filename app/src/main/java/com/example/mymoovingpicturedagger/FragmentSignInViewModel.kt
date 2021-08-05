package com.example.mymoovingpicturedagger

import androidx.lifecycle.ViewModel
import com.example.mymoovingpicturedagger.repozitory.Repozitory
import javax.inject.Inject

class FragmentSignInViewModel@Inject constructor(private val coordddrepo: Repozitory
):ViewModel() {

    suspend fun logInUser(username:String,password:String){
        coordddrepo.logInUserRep(username,password)
    }
}