package com.nativework.covid19vaccinetracker.networks

import android.content.Context
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.nativework.covid19vaccinetracker.BuildConfig
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Network module class represents the Retrofit client
 * building and provisioning of all the dependencies required  for API calling.
 *
 * @author Gauravkumar Mishra
 */
@Module
class NetworkModule(private var cacheFile: File, private var context: Context) {

    @Provides
    @Singleton
    internal fun provideCall(): Retrofit {
        var cache: Cache? = null
        try {
            cache = Cache(cacheFile, (10 * 1024).toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val okHttpClient = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            okHttpClient
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }
        okHttpClient.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("User-Agent", "Onwill Android")
                .header("Accept-Language","en_US")
                .header("Content-Type", "application/json")
                .header("Cache-Control", String.format("max-age=%d", BuildConfig.CACHETIME))
                .build()

            chain.proceed(request)
        }
            .cache(cache)
            .readTimeout(BuildConfig.READTIMEOUT.toLong(), TimeUnit.SECONDS)
            .connectTimeout(BuildConfig.CONNECTIONTIMEOUT.toLong(), TimeUnit.SECONDS)


        val client = okHttpClient.build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASEURL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()

    }

    @Provides
    @Singleton
    fun providesNetworkService(
        retrofit: Retrofit
    ): NetworkService {
        return retrofit.create(NetworkService::class.java)
    }

    @Provides
    @Singleton
    fun providesService(
        networkService: NetworkService
    ): Service {
        return Service(networkService)
    }

    @Provides
    @Singleton
    fun providesNetworkServiceImpl(
        networkService: NetworkService
    ): NetworkServiceImpl {
        return NetworkServiceImpl(networkService)
    }
}