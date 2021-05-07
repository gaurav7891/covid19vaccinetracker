package com.nativework.covid19vaccinetracker.networks

import com.nativework.covid19vaccinetracker.models.ConfirmOTPRequest
import com.nativework.covid19vaccinetracker.models.ConfirmOTPResponse
import com.nativework.covid19vaccinetracker.models.GenerateOTPRequest
import com.nativework.covid19vaccinetracker.models.GenerateOTPResponse
import io.reactivex.Single

/**
 * This class represents implementation of the all APIs which is declared in the NetworkService interface
 *
 * @author Gauravkumar Mishra
 */
class NetworkServiceImpl(private val mNetworkService: NetworkService) : NetworkService {
    override fun generateOTP(request: GenerateOTPRequest): Single<GenerateOTPResponse> {
        return mNetworkService.generateOTP(request)
    }

    override fun confirmOTP(request: ConfirmOTPRequest): Single<ConfirmOTPResponse> {
        return mNetworkService.confirmOTP(request)
    }


}