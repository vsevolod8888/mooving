package com.example.mymoovingpicturedagger.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CoordDao {
    @Query("select * from route")
    fun getRoutes(): LiveData<List<RouteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) //перезаписывает если одинак. айдишка
    suspend fun insertRoute(r: RouteEntity)

    @Query("DELETE FROM route")
    suspend fun deleteAllRoutes()

    @Query("DELETE FROM route WHERE id = :id")
    suspend fun deleteRouteById(id: Long)                     // было id: Int

    @Query("DELETE FROM coord WHERE recordNumber = :recordNumberId")
    suspend fun deleteCoordByRecordNumber(recordNumberId: Long)  //было recordNumber: Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoordList(coordlist: List<CoordinatesEntity>)

    @Query("SELECT * FROM coord WHERE recordNumber =:routeId")         // получ. лист CoordinatesEntity по recordNumber
    suspend fun getCoordtList(routeId: Int): List<CoordinatesEntity>?

    @Query("SELECT * FROM route WHERE id =:routeId")         // получ. лист CoordinatesEntity по recordNumber
    suspend fun getRouteById(routeId: Long): RouteEntity?            //біло routeId: Int

    @Query("SELECT * FROM coord WHERE recordNumber =:routeId")         // получ. лист CoordinatesEntity по recordNumber
    fun getCoordtListLiveData(routeId: Int): LiveData<List<CoordinatesEntity>>

    @Insert//(onConflict = OnConflictStrategy.)
    suspend fun insertCoord(c: CoordinatesEntity)          // suspend

    @Query("DELETE FROM coord")
    suspend fun clear()

    @Query("DELETE FROM route")
    suspend fun clear2()

    @Query("SELECT * FROM coord ORDER BY id DESC LIMIT 1")
    suspend fun getLastCoord(): CoordinatesEntity?

    @Query("SELECT * FROM coord ORDER BY id DESC")
    fun getAllCoords(): LiveData<List<CoordinatesEntity>>                                     //LiveData<>

    @Query("SELECT MAX(recordNumber) FROM coord ")
    suspend fun getLastRecordNumber(): Long?      // было Int LiveData<   >                                     // получить последний номер записи

    @Query("SELECT * FROM coord WHERE recordNumber=:recordNumberId ORDER BY checktime ")//
    fun getListByUnicalRecordNumber(recordNumberId: Long?): LiveData<List<CoordinatesEntity>>   // LiveData<Integer?>    список координат по определенному номеру записи

    @Query("SELECT * FROM coord WHERE recordNumber=:recordNumberId ORDER BY checktime ")//
    suspend fun getListByUnicalRecordNumberSuspend(recordNumberId: Long?): List<CoordinatesEntity> //было recordNumberID: Int

    @Query("SELECT DISTINCT recordNumber FROM coord ORDER BY recordNumber ")
    fun getOnlyRecordNumbersList(): LiveData<List<Int?>>                      // сптсок из recordNumber

    @Query("SELECT DISTINCT id FROM route ORDER BY id ")                // список id маршрутов
    suspend fun getOnlyIdList(): List<Long>

    @Query("SELECT COUNT (*) FROM coord")
    suspend fun getCountNumberOfRecords(): Int                                 // Посчитать к-во записей в БД suspend

    @Query("SELECT COUNT (*) FROM route")
    fun getCountNumberOfRecords2(): Int
}