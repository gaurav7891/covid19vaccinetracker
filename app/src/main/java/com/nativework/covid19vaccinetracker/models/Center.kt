package com.nativework.covid19vaccinetracker.models

data class Center(
    val address: String?,
    val block_name: String?,
    val center_id: Int?,
    val district_name: String?,
    val fee_type: String?,
    val from: String?,
    val lat: Int?,
    val long: Int?,
    val name: String?,
    val pincode: Int?,
    val sessions: List<Session>?,
    val state_name: String?,
    val to: String?
)