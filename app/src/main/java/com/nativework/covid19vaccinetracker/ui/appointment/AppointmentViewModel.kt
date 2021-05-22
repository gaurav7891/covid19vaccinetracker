package com.nativework.covid19vaccinetracker.ui.appointment

import com.nativework.covid19vaccinetracker.deps.DaggerInit
import com.nativework.covid19vaccinetracker.models.AppointmentResponse
import com.nativework.covid19vaccinetracker.models.Center
import com.nativework.covid19vaccinetracker.ui.HomeViewModel
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import java.util.*

class AppointmentViewModel : HomeViewModel() {

    private var appointmentRepository: AppointmentRepository


    init {
        DaggerInit.getDeps().inject(this)
        appointmentRepository = AppointmentRepository(service)
    }

    fun getAppointmentByPincodeAndDate(pincode: Int, date: String) {
        appointmentRepository.getAppointmentByPincodeAndDate(pincode, date,
            object : SingleObserver<AppointmentResponse> {
                override fun onSubscribe(d: Disposable) {
                    showProgress.value = true
                    compositeDisposable?.add(d)
                }

                override fun onSuccess(t: AppointmentResponse) {
                    showProgress.value = false
                    if (t.centers?.isEmpty()!!) {
                        isDataEmpty.value = true
                    } else {
                        centersList.setValue(t.centers as ArrayList<Center>)
                    }
                }

                override fun onError(e: Throwable) {
                    showProgress.value = false
                    errorMessage.value = e
                }
            })
    }
}