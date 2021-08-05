package com.example.mymoovingpicturedagger.apiservice

import com.example.mymoovingpicturedagger.*
import com.example.mymoovingpicturedagger.dto.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


private val logging = HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
private val httpClientBuilder = OkHttpClient.Builder().apply { addInterceptor(logging) }
var BASE_URL: String = "http://46.219.125.246:9191"
var retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(httpClientBuilder.build())
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface OurApiService {
    @POST("/api/tracking/track") //
    suspend fun addRoute(@Body newRoute: SendRouteRequest,@Header("Authorization")token:String): Response<Unit> // во все запросы подобавлять

    @GET("/api/tracking/tracks") //
    suspend fun downloadRoutes(@Header("Authorization")token:String): Response<List<SendRouteArchiveResponse>>

    @HTTP(method = "DELETE", path = "/api/tracking/track/{recordNumber}", hasBody = true)
    suspend fun deleteOneRoute(@Path("recordNumber") recordNumber: Long,@Header("Authorization")token:String): Response<Unit>//Response<List<SendRouteArchive>>

    @GET("/api/tracking/coordinates/{recordNumber}") //
    suspend fun downloadOneRouteByRecordnumber(@Path("recordNumber") recordNumber: Long,@Header("Authorization")token:String): Response<SendRouteRequest>

    @POST("/api/auth/signup") //
    suspend fun addNewUser(@Body user : RegistrationNewUserRequest ): Response<Unit>

    @POST("/api/auth/signin") //
    suspend fun logIn(@Body user : ExistingUserRequest ): Response<FromServerLogInResponse>

    @GET("/api/auth/me") //
    suspend fun checkIfUserEnter(@Header("Authorization")token:String): Response<CheckAmIEnterResponse>

    @Multipart
    @POST("/api/auth/avatar") //
    suspend fun upLoadImageOnServer(@Part imageFile: MultipartBody.Part, @Header("Authorization")token:String): Response<WhenUploadPhotoResponse>

}

object WeatherApi {
    val retrofitService: OurApiService by lazy {
        retrofit.create(OurApiService::class.java)
    }
}