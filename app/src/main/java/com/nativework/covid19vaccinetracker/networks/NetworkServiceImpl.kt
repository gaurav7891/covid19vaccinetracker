package com.nativework.covid19vaccinetracker.networks

import com.nativework.covid19vaccinetracker.models.*
import com.nativework.covid19vaccinetracker.models.locality.DistrictList
import com.nativework.covid19vaccinetracker.models.*
import io.reactivex.Single
import retrofit2.Call
import java.util.*

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

    override fun getDistrict(stateId: String): Single<DistrictList> {
        return mNetworkService.getDistrict(stateId)
    }

    override fun getCalendarByDistrict(
        districtID: String,
        date: String
    ): Single<GetCalendarByDistrictResponse> {
        return mNetworkService.getCalendarByDistrict(districtID, date)
    }

    override fun getCenterListFromDistrict(districtID: String, date: String): Call<GetCalendarByDistrictResponse> {
       return mNetworkService.getCenterListFromDistrict(districtID, date)
    }

    override fun getAppointmentByPincodeAndDate(
        pincode: Int,
        date: String
    ): Single<AppointmentResponse> {
        return mNetworkService.getAppointmentByPincodeAndDate(pincode,date)
    }


}