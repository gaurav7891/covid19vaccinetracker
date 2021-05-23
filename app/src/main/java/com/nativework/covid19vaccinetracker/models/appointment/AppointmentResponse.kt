package com.nativework.covid19vaccinetracker.models.appointment

import com.nativework.covid19vaccinetracker.models.Center

data class AppointmentResponse(
    val centers: List<Center>?
)