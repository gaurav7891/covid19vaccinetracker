package com.nativework.covid19vaccinetracker.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class GetCalendarByDistrictResponse(
    val centers: List<Center>?
)

@Parcelize
data class Center(
    val address: String?,
    val address_l: String?,
    val block_name: String?,
    val block_name_l: String?,
    val center_id: Int?,
    val district_name: String?,
    val district_name_l: String?,
    val fee_type: String?,
    val from: String?,
    val lat: Double?,
    val long: Double?,
    val name: String?,
    val name_l: String?,
    val pincode: String?,
    val sessions: List<Session>?,
    val state_name: String?,
    val state_name_l: String?,
    val to: String?,
    val vaccine_fees: List<VaccineFee>?
) : Parcelable

@Parcelize
data class VaccineFee(
    val fee: String?,
    val vaccine: String?
) : Parcelable

@Parcelize
data class Session(
    val available_capacity: Int?,
    val available_capacity_dose1: Int?,
    val available_capacity_dose2: Int?,
    val date: String?,
    val min_age_limit: Int?,
    val session_id: String?,
    val slots: List<String>?,
    val vaccine: String?
) : Parcelable