package com.nativework.covid19vaccinetracker.ui.otp

import com.nativework.covid19vaccinetracker.models.ConfirmOTPRequest
import com.nativework.covid19vaccinetracker.models.ConfirmOTPResponse
import com.nativework.covid19vaccinetracker.models.GenerateOTPRequest
import com.nativework.covid19vaccinetracker.models.GenerateOTPResponse
import com.nativework.covid19vaccinetracker.networks.NetworkServiceImpl
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class OTPRepository(private val service: NetworkServiceImpl) {
    fun generateOTP(
        request: GenerateOTPRequest,
        singleObserver: SingleObserver<GenerateOTPResponse>
    ) {
        service.generateOTP(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(singleObserver)
    }

    fun confirmOTP(request: ConfirmOTPRequest, singleObserver: SingleObserver<ConfirmOTPResponse>) {

        service.confirmOTP(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(singleObserver)
    }

}
