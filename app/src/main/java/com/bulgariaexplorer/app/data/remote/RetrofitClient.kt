package com.bulgariaexplorer.app.data.remote

import com.bulgariaexplorer.app.data.local.TokenManager
import com.bulgariaexplorer.app.data.remote.api.AdminApi
import com.bulgariaexplorer.app.data.remote.api.AuthApi
import com.bulgariaexplorer.app.data.remote.api.FavoriteApi
import com.bulgariaexplorer.app.data.remote.api.LeaderboardApi
import com.bulgariaexplorer.app.data.remote.api.MissionApi
import com.bulgariaexplorer.app.data.remote.api.NotificationApi
import com.bulgariaexplorer.app.data.remote.api.PoiApi
import com.bulgariaexplorer.app.data.remote.api.UserApi
import com.bulgariaexplorer.app.data.remote.api.VisitApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Use 10.0.2.2 for Android Emulator to access localhost
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private var retrofit: Retrofit? = null

    private fun getRetrofit(tokenManager: TokenManager): Retrofit {
        if (retrofit == null) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(tokenManager))
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun init(tokenManager: TokenManager) {
        getRetrofit(tokenManager)
    }

    val authApi: AuthApi get() = retrofit!!.create(AuthApi::class.java)
    val userApi: UserApi get() = retrofit!!.create(UserApi::class.java)
    val poiApi: PoiApi get() = retrofit!!.create(PoiApi::class.java)
    val favoriteApi: FavoriteApi get() = retrofit!!.create(FavoriteApi::class.java)
    val visitApi: VisitApi get() = retrofit!!.create(VisitApi::class.java)
    val leaderboardApi: LeaderboardApi get() = retrofit!!.create(LeaderboardApi::class.java)
    val missionApi: MissionApi get() = retrofit!!.create(MissionApi::class.java)
    val adminApi: AdminApi get() = retrofit!!.create(AdminApi::class.java)
    val notificationApi: NotificationApi get() = retrofit!!.create(NotificationApi::class.java)
}
