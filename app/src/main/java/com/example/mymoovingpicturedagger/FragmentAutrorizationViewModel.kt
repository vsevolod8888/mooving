package com.example.mymoovingpicturedagger

import androidx.lifecycle.ViewModel
import com.example.mymoovingpicturedagger.dto.RegistrationNewUserRequest
import com.example.mymoovingpicturedagger.repozitory.Repozitory
import javax.inject.Inject

class FragmentAutrorizationViewModel@Inject constructor(private val coordddrepo: Repozitory
): ViewModel() {
    suspend fun addNewUserVM(user: RegistrationNewUserRequest):Boolean{
       return coordddrepo.addNewUser(user)
    }
}