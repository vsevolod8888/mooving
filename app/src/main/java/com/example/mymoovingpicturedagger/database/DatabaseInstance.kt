package com.example.mymoovingpicturedagger.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CoordinatesEntity::class,RouteEntity::class], version = 2,exportSchema = false)
abstract class CoordinatcchicDatabase : RoomDatabase(){
    abstract val coorddao: CoordDao
}
private lateinit var INSTANCE: CoordinatcchicDatabase  // переменная INSTANCE для хранения одноэлементного объекта БД (чтобы не открылись неск.экз.бд)
fun getDatabase(context: Application): CoordinatcchicDatabase{
    synchronized(CoordinatesEntity::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                CoordinatcchicDatabase::class.java,
                "coordinates").fallbackToDestructiveMigration().build()
        }
    }
    return INSTANCE
}