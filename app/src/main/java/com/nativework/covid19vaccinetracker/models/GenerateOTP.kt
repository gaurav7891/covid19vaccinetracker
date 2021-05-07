package com.nativework.covid19vaccinetracker.models

data class GenerateOTPRequest(
    val mobile: String
)

data class GenerateOTPResponse(
    val txnId: String
)

data class ConfirmOTPRequest(
    val otp:String,
    val txnId: String
)

data class ConfirmOTPResponse(
    val token:String
)