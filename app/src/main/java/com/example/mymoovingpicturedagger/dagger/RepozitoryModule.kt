package com.example.mymoovingpicturedagger.dagger

import android.content.Context
import androidx.room.Room
import com.example.mymoovingpicturedagger.database.CoordinatcchicDatabase
import com.example.mymoovingpicturedagger.repozitory.Repozitory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
@Module
class RepozitoryModule (var context: Context){
    @Provides //@Provides указывает, что метод является поставщиком объекта и компонент может использовать его, чтобы получить объект.
    @Singleton
    fun provideContext( ): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideDatabase(context: Context): CoordinatcchicDatabase {
        val INSTANCE = Room.databaseBuilder(context.applicationContext,
            CoordinatcchicDatabase::class.java,
            "coordinates").fallbackToDestructiveMigration().build()
        return INSTANCE
    }
    @Provides
    @Singleton
    fun providesRepozitory(db:CoordinatcchicDatabase,context: Context): Repozitory {
        return Repozitory(db,context) //в модуле создаются зависимости
    }
}