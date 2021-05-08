package com.nativework.covid19vaccinetracker.networks

import com.nativework.covid19vaccinetracker.models.ConfirmOTPRequest
import com.nativework.covid19vaccinetracker.models.ConfirmOTPResponse
import com.nativework.covid19vaccinetracker.models.GenerateOTPRequest
import com.nativework.covid19vaccinetracker.models.GenerateOTPResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * This interface represents APIs declaration
 *
 * @author Gauravkumar Mishra
 */
interface NetworkService {

    @POST("v2/auth/public/generateOTP")
    fun generateOTP(@Body request: GenerateOTPRequest): Single<GenerateOTPResponse>

    @POST("v2/auth/public/confirmOTP")
    fun confirmOTP(@Body request: ConfirmOTPRequest):Single<ConfirmOTPResponse>

}
