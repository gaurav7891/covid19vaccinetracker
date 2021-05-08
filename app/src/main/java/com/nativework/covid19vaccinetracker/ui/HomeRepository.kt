package com.nativework.covid19vaccinetracker.ui

import com.nativework.covid19vaccinetracker.models.GetCalendarByDistrictResponse
import com.nativework.covid19vaccinetracker.models.locality.DistrictList
import com.nativework.covid19vaccinetracker.networks.NetworkServiceImpl
import io.reactivex.Scheduler
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HomeRepository(private val service: NetworkServiceImpl) {

    fun getCalendarByDistrict(
            districtId: String,
            date: String,
            observer: SingleObserver<GetCalendarByDistrictResponse>
    ) {
        service.getCalendarByDistrict(districtId, date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)

    }

    fun getDistrictList(stateId: String, singleObserver: SingleObserver<DistrictList>) {
        service.getDistrict(stateId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(singleObserver)
    }
}