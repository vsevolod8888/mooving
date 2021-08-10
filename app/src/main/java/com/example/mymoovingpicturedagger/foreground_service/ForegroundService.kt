package com.example.mymoovingpicturedagger.foreground_service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.navigation.NavDeepLinkBuilder
import com.example.mymoovingpicturedagger.MainActivity
import com.example.mymoovingpicturedagger.R
import com.example.mymoovingpicturedagger.dagger.App
import com.example.mymoovingpicturedagger.database.CoordinatesEntity
import com.example.mymoovingpicturedagger.database.RouteEntity
import com.example.mymoovingpicturedagger.repozitory.Repozitory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ticker
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ForegroundService() : LifecycleService(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob()
    var job: Job? = null
    private val CHANNEL_ID = "ForegroundService Kotlin"

    @Inject
    lateinit var coordddrepo: Repozitory

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            applicationContext
        )
    }
    val ACTION_STOP_SERVICE: String = "Stop"
    var numbOfRecord: Long? = null
    var timeStartRoute: Long? = null
    lateinit var nameOfRoute: String
    var idd: Long? = null


    companion object {
        var timeRepeat: Long? = null
        fun startService(
            context: Context,
            message: String,
            routeId: String? = null
        ) {
            val startIntent = Intent(context, ForegroundService::class.java)
            startIntent.putExtra("inputExtra", message)
            if (routeId != null) {
                startIntent.putExtra(
                    "inputttt",
                    routeId.toString()
                )
            }

            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, ForegroundService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)


        if (intent!!.action != null && intent.action.equals("STOP_ACTION")) {
            stopForeground(true);
        }
        nameOfRoute =
            intent.getStringExtra("inputExtra").toString()
        idd = intent.getStringExtra("inputttt")?.toLong()
        Log.d("GGGG", idd.toString())
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        (application as App).getappComponent().inject(this)
        val notification: Notification =
            getMyActivityNotification("Гуляем") // сразу должна біть нотификация, без ожидания

        startForeground(1, notification)
        startTimer()

    }

    fun getMyActivityNotification(s: String): Notification {

        val pendingIntent = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.navigation)
            .setDestination(R.id.mapNewRoute)
            .createPendingIntent()

        val stopSelf = Intent(this, ForegroundService::class.java)
        stopSelf.action = this.ACTION_STOP_SERVICE
        val pStopSelf =
            PendingIntent.getService(this, 0, stopSelf, PendingIntent.FLAG_CANCEL_CURRENT)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .addAction(R.mipmap.ic_stop_service, "Stop", pStopSelf)
            .setContentTitle("Запущено приложение по составлению маршрута")
            .setContentText(s)
            .setSmallIcon(R.mipmap.ic_loc)
            .setContentIntent(pendingIntent)
            .setOngoing(false)
            .build()
        return notification
    }

    fun startTimer() {
        Log.d("ОШИБКА", "старттаймер запущен")
        timeRepeat?.let { setupTimer(it) }
    }

    fun setupTimer(m: Long) {
        job?.cancel()
        job = launch {
            val tickerChannel = ticker(delayMillis = m, initialDelayMillis = 1)
            Log.d("ОШИБКА", "Запущено RecurentWork")
            if (idd != null) {
                numbOfRecord = idd
            } else {
                if (coordddrepo.isEmptyy() == true) {      // если КООРДИНАТ пуст
                    numbOfRecord = System.currentTimeMillis()  //0
                    timeStartRoute = System.currentTimeMillis()
                } else {
                    numbOfRecord =
                        coordddrepo.lastNumberOfList()                  // правильно ли я сделал???????????????????????????????????

                    numbOfRecord = System.currentTimeMillis()//numbOfRecord?.plus(1)
                    timeStartRoute = System.currentTimeMillis()
                }
            }

            for (event in tickerChannel) {
                try {
                    Log.d("ОШИБКА", "Вкл. минута")
                    if (ActivityCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.ACCESS_FINE_LOCATION                                            // грубая локация
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION                                          // точная локация
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.d("ОШИБКА", "Нету разрешений")
                        return@launch
                    }
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            val lattt: Double? = location?.latitude
                            val loggg: Double? = location?.longitude
                            Log.d(
                                "ОШИБКА",
                                "$numbOfRecord != null && $lattt != null && $loggg != null"
                            )
                            launch {
                                if (numbOfRecord != null && lattt != null && loggg != null) {
                                    saveCurrentCoordinates(                                        // сохр.координату
                                        lattt,
                                        loggg,
                                        System.currentTimeMillis(),
                                        numbOfRecord!!
                                    )
                                }                  //, numbOfRecord!!
                            }
                        }
                } catch (e: Exception) {
                    Log.d("ОШИБКА", "JHUOH")
                }
            }
        }
    }

    suspend fun saveNewRoute(
        name: String
    ) {
        if (coordddrepo.getCoordinatesByRecordNumberSus(numbOfRecord!!).size > 0) {
            val newRoute =
                RouteEntity(
                    id = numbOfRecord!!,
                    checkTime = timeStartRoute!!,
                    recordRouteName = name,
                    isClicked = false
                )
            coordddrepo.insertRoute(newRoute)
        } else {
            Toast.makeText(
                this,
                "Марштут не сохранён, координаты не были записаны",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    suspend fun saveCurrentCoordinates(
        lat: Double,
        lon: Double,
        t: Long,
        n: Long
    ) {

        val newcoord = CoordinatesEntity(
            id = 0,
            checkTime = t,
            recordNumber = n,
            Lattitude = lat,
            Longittude = lon
        )
        Log.d("ОШИБКА", newcoord.toString())

// все что внутри withContext(Dispatchers.IO)  в другом потоке
        coordddrepo.insertCoord(newcoord)
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        GlobalScope.launch {              //GlobalScope - в ланче довыполняется до конца
            if (nameOfRoute.isNotEmpty() && numbOfRecord != null) {
                saveNewRoute(nameOfRoute)
            }
        }
        cancel()
    }
}