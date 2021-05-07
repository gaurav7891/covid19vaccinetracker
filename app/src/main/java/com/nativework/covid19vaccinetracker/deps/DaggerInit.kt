package com.nativework.covid19vaccinetracker.deps

import com.nativework.covid19vaccinetracker.MyApplication
import com.nativework.covid19vaccinetracker.networks.NetworkModule

class DaggerInit {

    companion object {

        private lateinit var deps: Deps

        @JvmStatic
        fun initializeComponent(application: MyApplication) {
            deps = DaggerDeps.builder()
                .networkModule(NetworkModule(application.cacheDir, application.applicationContext))
                .build()
        }

        @JvmStatic
        fun getDeps(): Deps {
            return deps
        }
    }
}