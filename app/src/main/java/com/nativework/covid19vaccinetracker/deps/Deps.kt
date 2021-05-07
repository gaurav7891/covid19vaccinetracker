package com.nativework.covid19vaccinetracker.deps

import com.nativework.covid19vaccinetracker.MyApplication
import com.nativework.covid19vaccinetracker.networks.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface Deps {
    fun inject(myApplication: MyApplication)
}