package com.bulgariaexplorer.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.bulgariaexplorer.app.data.local.TokenManager
import com.bulgariaexplorer.app.data.remote.RetrofitClient
import com.bulgariaexplorer.app.notifications.NotificationWorker
import okhttp3.OkHttpClient

class BulgariaExplorerApp : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(TokenManager(this))
        NotificationWorker.createNotificationChannel(this)
        NotificationWorker.schedule(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val originalUrl = chain.request().url.toString()
                        val fixedUrl = originalUrl.replace("://localhost:", "://10.0.2.2:")
                        val request = chain.request().newBuilder()
                            .url(fixedUrl)
                            .header("User-Agent", "BulgariaExplorer/1.0 (Android)")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            }
            .build()
    }
}
