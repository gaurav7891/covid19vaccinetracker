package com.nativework.covid19vaccinetracker.models.locality

class CitiesList : ArrayList<Cities>()

data class Cities(
    val district_id: Int?,
    val district_name: String?,
    val state_id: Int?
)

class StatesList : ArrayList<States>()

data class States(
    val stateId: Int?,
    val stateName: String?
)


data class District(
    val district_id: Int?,
    val district_name: String?
)

data class DistrictList(
    val districts: List<District>?,
    val ttl: Int?
)