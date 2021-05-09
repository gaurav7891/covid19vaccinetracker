package com.nativework.covid19vaccinetracker.models

data class SavedPreferences(
    val type: String?,
    val districtId: String?,
    val district: String?,
    val stateId: String?,
    val state: String?,
    val date: String?
)