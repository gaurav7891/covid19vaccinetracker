package com.nativework.covid19vaccinetracker.models

data class Session(
    val available_capacity: Int?,
    val date: String?,
    val min_age_limit: Int?,
    val session_id: String?,
    val slots: List<String>?,
    val vaccine: String?
)