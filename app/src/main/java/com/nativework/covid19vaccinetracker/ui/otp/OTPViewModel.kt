package com.nativework.covid19vaccinetracker.ui.otp

import androidx.lifecycle.MutableLiveData
import com.nativework.covid19vaccinetracker.base.BaseViewModel
import com.nativework.covid19vaccinetracker.deps.DaggerInit
import com.nativework.covid19vaccinetracker.models.ConfirmOTPRequest
import com.nativework.covid19vaccinetracker.models.ConfirmOTPResponse
import com.nativework.covid19vaccinetracker.models.GenerateOTPRequest
import com.nativework.covid19vaccinetracker.models.GenerateOTPResponse
import com.nativework.covid19vaccinetracker.networks.NetworkServiceImpl
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class OTPViewModel : BaseViewModel() {

    @Inject
    lateinit var service: NetworkServiceImpl
    private var otpRepository: OTPRepository

    val txnId = MutableLiveData<String>()
    val token = MutableLiveData<String>()


    init {
        DaggerInit.getDeps().inject(this)
        otpRepository = OTPRepository(service)
    }

    fun generateOTP(request: GenerateOTPRequest) {
        otpRepository.generateOTP(request, object : SingleObserver<GenerateOTPResponse> {
            override fun onSubscribe(d: Disposable) {
                showProgress.value = true
                compositeDisposable?.add(d)
            }

            override fun onSuccess(t: GenerateOTPResponse) {
                showProgress.value = false
                if (t != null) {
                    txnId.value = t.txnId
                }
            }

            override fun onError(e: Throwable) {
                showProgress.value = false
                errorMessage.value = e
            }

        })
    }

    fun confirmOTP(request: ConfirmOTPRequest) {
        otpRepository.confirmOTP(request, object : SingleObserver<ConfirmOTPResponse> {
            override fun onSubscribe(d: Disposable) {
                showProgress.value = true
                compositeDisposable?.add(d)
            }

            override fun onSuccess(t: ConfirmOTPResponse) {
                showProgress.value = false
                if (t != null) {
                    token.value = t.token
                }
            }

            override fun onError(e: Throwable) {
                showProgress.value = false
                errorMessage.value = e
            }

        })
    }
}