package com.example.mymoovingpicturedagger.repozitory


import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.room.withTransaction
import com.example.mymoovingpicturedagger.apiservice.*
import com.example.mymoovingpicturedagger.database.*
import com.example.mymoovingpicturedagger.domain.CoordinatesDomain
import com.example.mymoovingpicturedagger.domain.RouteDomain
import com.example.mymoovingpicturedagger.domain.SendRouteDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.Context
import android.net.Uri
import com.example.mymoovingpicturedagger.dto.*
import com.example.mymoovingpicturedagger.helpers.getPath
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class Repozitory(val database: CoordinatcchicDatabase, val context: Context) {
    var sharedPref: SharedPreferences? = null
    val MyPREFERENCES = "MyPrefs"
    val KEYTOKEN: String = "key"
    val KEY_BUTTON_TIME_REPEAT: String = "button timerepeat"
    val KEY_ROUTE_NAME = "myKey"

    var sentroutelistdomain: MutableLiveData<List<SendRouteDomain>> = MutableLiveData()
    var enterUser: MutableLiveData<CheckAmIEnterResponse?> = MutableLiveData()

    val routeslist: LiveData<List<RouteDomain>> =
        Transformations.map(database.coorddao.getRoutes()) {
            it.asDomainRouteModel() //  используйте Transformations.map для преобразования списка объектов базы данных в список объектов домена
        }

    fun getSharedPreferencesTimeRepeat(): Int? {
        val lastButtonState: Int? = sharedPref?.getInt(KEY_BUTTON_TIME_REPEAT, 6000)
        return lastButtonState
    }

    fun putSharedPreferencesTimeRepeat(pref: Int) {
        with(sharedPref?.edit()) {
            this?.putInt(KEY_BUTTON_TIME_REPEAT, pref)
            this?.apply()
        }
    }

    fun getSharedPreferencesRouteName(): String? {
        val routeName: String? = sharedPref?.getString(KEY_ROUTE_NAME, "Имя по умолчанию")
        return routeName
    }

    fun putSharedPreferencesRouteName(pref: String) {
        with(sharedPref?.edit()) {
            this?.putString(KEY_ROUTE_NAME, pref)
            this?.apply()
        }
    }

    fun newRouteList(routeId: Int): LiveData<List<CoordinatesDomain>> =
        Transformations.map(database.coorddao.getCoordtListLiveData(routeId)) {
            it.asDomainCoordinatesModel()
        }

    suspend fun getRouteById(id: Long): RouteDomain? {          // было Int
        return database.coorddao.getRouteById(id)?.asDomainRouteModel()

    }

    suspend fun makeTokenNull() {
        with(sharedPref?.edit()) {
            this?.putString(KEYTOKEN, null)
            this?.apply()
        }
    }

    fun getToken(): String? {
        val token = sharedPref?.getString(KEYTOKEN, null)
        if (token == null) {
            return null
        } else {
            return token
        }
    }

    suspend fun insertRoute(newRoute: RouteEntity) {
        database.coorddao.insertRoute(newRoute)
    }

    suspend fun deleteRouteAndRecordNumberTogether(id: Long) {

        database.withTransaction {
            database.coorddao.deleteRouteById(id)
            database.coorddao.deleteCoordByRecordNumber(id)
        }
    }

    suspend fun insertCoord(c: CoordinatesEntity) {
        database.coorddao.insertCoord(c)
    }

    suspend fun deleteAllRoutes() {
        database.coorddao.deleteAllRoutes()
    }

    suspend fun deleteRouteById(id: Long) {// было id: Int
        database.coorddao.deleteRouteById(id)
    }

    suspend fun deleteCoordByRecordNumber(recordNumber: Long) {//было recordNumber: Int
        database.coorddao.deleteCoordByRecordNumber(recordNumber)
    }

    suspend fun lastNumberOfList(): Long? {           // было Int
        return try {
            database.coorddao.getLastRecordNumber()//
        } catch (e: Exception) {
            Log.d("OOO", e.message!!)
            null
        }                                          //  используйте Transformations.map для преобразования спискаа
    }

    fun getCoordinatesByRecordNumber(recordNumber: Long): LiveData<List<CoordinatesDomain>> =
        Transformations.map(database.coorddao.getListByUnicalRecordNumber(recordNumber)) {
            it.asDomainCoordinatesModel()
        }

    fun getAllCoords(): LiveData<List<CoordinatesDomain>> = Transformations.map(
        database.coorddao.getAllCoords()
    ) {
        it.asDomainCoordinatesModel()
    }

    suspend fun getCoordinatesByRecordNumberSus(recordNumber: Long): List<CoordinatesDomain> =
        database.coorddao.getListByUnicalRecordNumberSuspend(recordNumber)
            .asDomainCoordinatesModel()

    suspend fun getOnlyIdList(): List<Long> {
        return database.coorddao.getOnlyIdList()
    }

    suspend fun deleteList() {
        database.coorddao.clear()
        database.coorddao.clear2()
    }

    suspend fun isEmptyy(): Boolean {
        if (database.coorddao.getCountNumberOfRecords() > 0) {
            return false
        } else {
            return true
        }
    }

    suspend fun deleteRouteFromServer(recordNumber: Long) {
        val token = sharedPref?.getString(KEYTOKEN, null)
        token?.let {

            val ourApiService: OurApiService = retrofit.create(OurApiService::class.java)
            val requestCall = try {
                ourApiService.deleteOneRoute(recordNumber, "Bearer " + token)
            } catch (e: java.lang.Exception) {
                Log.e("OOO", "${e.message}")
                null
            }
            if (requestCall?.isSuccessful == true) {
                Log.e("OOO", "НЕ удалился")
            } else {
                Log.e("OOO", "УДАЛИЛСЯ")
            }
        }
    }

    suspend fun refreshServerRoutes() {
        sharedPref = context?.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
        val token = sharedPref?.getString(KEYTOKEN, null)
        token?.let {
            withContext(Dispatchers.IO) {
                val ourApiService: OurApiService = retrofit.create(OurApiService::class.java)
                val requestCall = try {
                    ourApiService.downloadRoutes("Bearer " + token)
                } catch (e: java.lang.Exception) {
                    Log.e("OOO", "${e.message}")
                    null
                }
                if (requestCall?.isSuccessful == true) {
                    val sentroutelistarchive = requestCall.body()
                    sentroutelistarchive?.let {
                        sentroutelistdomain.postValue(asDomainnnSendRouteArchiveResponseModel(it)) // postValue это для бэкграунт потока в отличии от value
                    }
                    Log.d("OOO", "НОРМАЛЬНО")
                } else {
                    Log.d("OOO", "ПЛОХО")
                }
            }
        }
    }

    suspend fun addNewUser(newUser: RegistrationNewUserRequest): Boolean {
        val ourApiService: OurApiService = retrofit.create(OurApiService::class.java)
        val requestCall = try {
            ourApiService.addNewUser(newUser) //token из преференсов
        } catch (e: java.lang.Exception) {
            Log.e("OOO", "${e.message}")
            null
        }
        if (requestCall?.isSuccessful == true) {
            Log.d("OOO", "Авторизация прошла успешно")
            return true
        } else {
            Log.d("OOO", "Не удалось авторизироваться")
            return false
        }
    }

    suspend fun downloadRouteFromServer(id: Long) {
        sharedPref = context?.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
        val token = sharedPref?.getString(KEYTOKEN, null)
        token?.let {
            withContext(Dispatchers.IO) {
                val ourApiService: OurApiService = retrofit.create(OurApiService::class.java)
                val requestCall = try {
                    ourApiService.downloadOneRouteByRecordnumber(id, "Bearer " + token)
                } catch (e: java.lang.Exception) {
                    Log.e("OOO", "${e.message}")
                    null
                }
                if (requestCall?.isSuccessful == true) {
                    val sentroutelistarchive = requestCall.body()
                    sentroutelistarchive.let {
                        if (it != null) {
                            var route = convertSentRouteToRouteEntity(it)
                            database.coorddao.insertRoute(route)
                            var coordlist = convertCoordDataToCoordinatesEntity(it)
                            database.coorddao.insertCoordList(coordlist)
                        }
                    }
                    Log.d("OOO", "НОРМАЛЬНО")
                } else {
                    Log.d("OOO", "ПЛОХО")
                }
            }
        }
    }

    suspend fun checkIfUserIsEnter() {
        sharedPref = context?.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
        val token = sharedPref?.getString(KEYTOKEN, null)
        if (token != null) {
            withContext(Dispatchers.IO) {
                val ourApiService: OurApiService = retrofit.create(OurApiService::class.java)
                val requestCall = try {
                    ourApiService.checkIfUserEnter("Bearer " + token)
                } catch (e: java.lang.Exception) {
                    Log.e("OOO", "${e.message}")
                    null
                }
                if (requestCall?.isSuccessful == true) {
                    val enterUserNow = requestCall.body()
                    enterUserNow?.let {
                        enterUser.postValue(it)
                    }
//
                    Log.d("OOO", "ЮЗЕР ЗАШ₴Л В АККАУНТ")

                } else {
                    enterUser.postValue(null)
                    Log.d("OOO", "ПЛОХО")

                }
            }


        } else {
            enterUser.postValue(null)
        }
    }

    suspend fun uploadPhotoOnServer(uri: Uri) {
        sharedPref = context?.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
        val token = sharedPref?.getString(KEYTOKEN, null)
        Log.e("OOO", "$token")
        if (token != null) {
            val file = File(context.getPath(uri)) // пропустить через вьюмодель и репозиторий
            val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            val filePart = MultipartBody.Part.createFormData("imageFile", file.name, requestBody)
            withContext(Dispatchers.IO) {
                val ourApiService: OurApiService = retrofit.create(OurApiService::class.java)
                val requestCall = try {
                    ourApiService.upLoadImageOnServer(filePart, "Bearer " + token)
                } catch (e: java.lang.Exception) {
                    Log.e("OOO", "${e.message}")
                    null
                }
                if (requestCall?.isSuccessful == true) {
                    val enterUserNow = requestCall.body()
                    Log.d("OOO", "Картинка")

                } else {
                    Log.d("OOO", "ПЛОХО")
                }
            }
        } else {
            enterUser.postValue(null)
        }
    }


    suspend fun onUploadRouteOnSErver(
        rrr: RouteEntity,
        sendRoute: SendRouteRequest
    ) { // проверить, если шаред преф пустой, то перекидывать на фрагмент логина и регистрации
        val token = sharedPref?.getString(KEYTOKEN, null)
        if (token != null) {
            val ourApiService: OurApiService = retrofit.create(OurApiService::class.java)
            val requestCall = try {
                ourApiService.addRoute(sendRoute, "Bearer " + token) //token из преференсов

            } catch (e: java.lang.Exception) {
                Log.e("OOO", "${e.message}")
                null
            }
            if (requestCall?.isSuccessful == true) {
                if (rrr != null) {
                    rrr.isClicked =
                        true                                            // перенести єто в изсаксесфул
                    database.coorddao.insertRoute(rrr)
                }

                Log.e("OOO", "маршрут загружен")
                //создать метод обновить поле и вызвать его отсюда
            } else {
                Log.e("OOO", "не получилось")
            }


        } else {
            Log.e("OOO", "token = null, поэтому не вышло")
        }
    }

    suspend fun logInUserRep(username: String, password: String) {
        val user = ExistingUserRequest(
            username = username,
            password = password
        )

        val ourApiService: OurApiService = retrofit.create(OurApiService::class.java)
        val requestCall = try {
            ourApiService.logIn(user) //token из преференсов
        } catch (e: java.lang.Exception) {
            Log.e("OOO", "${e.message}")
            null
        }
        if (requestCall?.isSuccessful == true) {
            val responseFromServer = requestCall.body()
            responseFromServer?.let {
                var token: String = it.token.toString()

                with(sharedPref?.edit()) {
                    this?.putString(KEYTOKEN, token)

                    this?.apply()
                }
            }


        } else {
            Log.e("OOO", "Вход не произведён")
        }
    }
}