package com.example.mymoovingpicturedagger.dagger

import android.app.Application

class App: Application() {
    lateinit var ourComponent: Component
    override fun onCreate() {
        super.onCreate()
        ourComponent =DaggerComponent.builder()
            .repozitoryModule(RepozitoryModule(this)) //передаю контекст в конструктор,в большом RepozitoryModule
            .build()
    }
    fun getappComponent(): Component {
        return ourComponent
    }

}