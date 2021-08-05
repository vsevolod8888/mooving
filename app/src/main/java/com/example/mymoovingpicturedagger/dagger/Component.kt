package com.example.mymoovingpicturedagger.dagger

import com.example.mymoovingpicture.*
import com.example.mymoovingpicturedagger.FragmentSignIn
import com.example.mymoovingpicturedagger.FragmentArchiveList
import com.example.mymoovingpicturedagger.FragmentAutrorization
import com.example.mymoovingpicturedagger.MainActivity
import com.example.mymoovingpicturedagger.foreground_service.ForegroundService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RepozitoryModule::class,ForAllViewModelModule::class])
interface Component {
    fun inject(fragmentCoordList: FragmentCoordList)
    fun inject(fserv: ForegroundService)
    fun inject(mapChosenRoute: MapChosenRoute)
    fun inject(mapDrawing: MapDrawing)
    fun inject(mapNewR: MapNewRoute)
    fun inject(mapAll: mapAll)
    fun inject(frArch: FragmentArchiveList)
    fun inject(mainActivity: MainActivity)
    fun inject(fragmSignIn: FragmentSignIn)
    fun inject(fragmentAutrorization: FragmentAutrorization)
    fun inject(enterRouteName: EnterRouteName)
    fun inject(fragmentNameDrawingRoot: FragmentNameDrawingRoot)
}