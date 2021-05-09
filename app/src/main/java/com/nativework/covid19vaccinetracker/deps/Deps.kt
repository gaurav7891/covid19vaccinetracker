package com.nativework.covid19vaccinetracker.deps

import com.nativework.covid19vaccinetracker.MyApplication
import com.nativework.covid19vaccinetracker.networks.NetworkModule
import com.nativework.covid19vaccinetracker.ui.appointment.AppointmentViewModel
import com.nativework.covid19vaccinetracker.ui.HomeViewModel
import com.nativework.covid19vaccinetracker.ui.otp.OTPViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface Deps {
    fun inject(myApplication: MyApplication)
    fun inject(otpViewModel: OTPViewModel)
    fun inject(homeViewModel: HomeViewModel)
    fun inject(appointmentViewModel: AppointmentViewModel)
}