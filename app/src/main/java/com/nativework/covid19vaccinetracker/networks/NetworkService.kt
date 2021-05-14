package com.nativework.covid19vaccinetracker.networks

import com.nativework.covid19vaccinetracker.models.*
import com.nativework.covid19vaccinetracker.models.locality.DistrictList
import com.nativework.covid19vaccinetracker.models.*
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.*
import java.util.*

/**
 * This interface represents APIs declaration
 *
 * @author Gauravkumar Mishra
 */
interface NetworkService {

    @POST("v2/auth/public/generateOTP")
    fun generateOTP(@Body request: GenerateOTPRequest): Single<GenerateOTPResponse>

    @POST("v2/auth/public/confirmOTP")
    fun confirmOTP(@Body request: ConfirmOTPRequest): Single<ConfirmOTPResponse>

    @GET("v2/admin/location/districts/{state_id}")
    fun getDistrict(@Path("state_id") stateId: String): Single<DistrictList>

    @GET("v2/appointment/sessions/public/calendarByDistrict")
    fun getCalendarByDistrict(
        @Query("district_id") districtID: String,
        @Query("date") date: String
    ): Single<GetCalendarByDistrictResponse>

    @GET("v2/appointment/sessions/public/calendarByDistrict")
    fun getCenterListFromDistrict(
            @Query("district_id") districtID: String,
            @Query("date") date: String
    ): Call<GetCalendarByDistrictResponse>

    // get Appointment by date and pincode
    @GET("v2/appointment/sessions/public/calendarByPin")
    fun getAppointmentByPincodeAndDate(
        @Query("pincode") pincode:Int,
        @Query("date")date : String):
            Single<AppointmentResponse>
}
