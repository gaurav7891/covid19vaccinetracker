package com.nativework.covid19vaccinetracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nativework.covid19vaccinetracker.base.BaseViewModel
import com.nativework.covid19vaccinetracker.base.SingleLiveEvent
import com.nativework.covid19vaccinetracker.deps.DaggerInit
import com.nativework.covid19vaccinetracker.models.GetCalendarByDistrictResponse
import com.nativework.covid19vaccinetracker.models.locality.District
import com.nativework.covid19vaccinetracker.models.locality.DistrictList
import com.nativework.covid19vaccinetracker.networks.NetworkServiceImpl
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class HomeViewModel : BaseViewModel() {

    @Inject
    lateinit var service: NetworkServiceImpl
    private var repository: HomeRepository
    var districtList = SingleLiveEvent<ArrayList<District>>()

    init {
        DaggerInit.getDeps().inject(this)
        repository = HomeRepository(service)
    }

    fun getDistrictList(stateId: String) {
        repository.getDistrictList(stateId, object : SingleObserver<DistrictList> {
            override fun onSubscribe(d: Disposable) {
                showProgress.value = true
                compositeDisposable?.add(d)
            }

            override fun onSuccess(t: DistrictList) {
                showProgress.value = false
                if (t.districts?.size!! > 0) {
                    districtList.setValue(t.districts as ArrayList<District>)
                }
            }

            override fun onError(e: Throwable) {
                showProgress.value = false
                errorMessage.value = e
            }

        })
    }

    fun getDistrictListData(): LiveData<ArrayList<District>> {
        return districtList
    }

    fun getCalendarByDistrict(districtId: String, date: String) {
        repository.getCalendarByDistrict(
            districtId,
            date,
            object : SingleObserver<GetCalendarByDistrictResponse> {
                override fun onSubscribe(d: Disposable) {
                    showProgress.value = true
                    compositeDisposable?.add(d)
                }

                override fun onSuccess(t: GetCalendarByDistrictResponse) {
                    showProgress.value = false
                   if (t.centers?.size!! > 0 ){

                   }
                }

                override fun onError(e: Throwable) {
                    showProgress.value = false
                    errorMessage.value = e
                }

            })
    }
}