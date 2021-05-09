package com.nativework.covid19vaccinetracker.ui.appointment

import com.nativework.covid19vaccinetracker.models.AppointmentResponse
import com.nativework.covid19vaccinetracker.networks.NetworkServiceImpl
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class AppointmentRepository(private val service: NetworkServiceImpl) {

    /** To get appointment by pincode and date*/

    fun getAppointmentByPincodeAndDate(
        pincode: Int,
        date: String,
        singleObserver: SingleObserver<AppointmentResponse>
    ) {
        service.getAppointmentByPincodeAndDate(pincode,date)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(singleObserver)

    }
}