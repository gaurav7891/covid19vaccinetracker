package com.nativework.covid19vaccinetracker

import android.app.Application
import android.content.Context
import com.nativework.covid19vaccinetracker.deps.DaggerInit
import com.nativework.covid19vaccinetracker.deps.Deps
import com.nativework.covid19vaccinetracker.networks.NetworkServiceImpl
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject


class MyApplication : Application() {
    @Inject
    lateinit var service: NetworkServiceImpl
    lateinit var deps: Deps

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        initDagger()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    private fun initDagger() {
        DaggerInit.initializeComponent(this)
        DaggerInit.getDeps().inject(this)
    }
}