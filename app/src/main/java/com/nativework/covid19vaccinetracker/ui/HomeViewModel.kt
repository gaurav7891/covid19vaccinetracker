package com.nativework.covid19vaccinetracker.ui

import androidx.lifecycle.LiveData
import com.nativework.covid19vaccinetracker.base.BaseViewModel
import com.nativework.covid19vaccinetracker.base.SingleLiveEvent
import com.nativework.covid19vaccinetracker.deps.DaggerInit
import com.nativework.covid19vaccinetracker.models.appointment.AppointmentResponse
import com.nativework.covid19vaccinetracker.models.Center
import com.nativework.covid19vaccinetracker.models.GetCalendarByDistrictResponse
import com.nativework.covid19vaccinetracker.models.locality.District
import com.nativework.covid19vaccinetracker.models.locality.DistrictList
import com.nativework.covid19vaccinetracker.networks.NetworkServiceImpl
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

open class HomeViewModel : BaseViewModel() {

    @Inject
    lateinit var service: NetworkServiceImpl
    private var repository: HomeRepository
    var districtList = SingleLiveEvent<ArrayList<District>>()
    var centersList = SingleLiveEvent<ArrayList<Center>>()
    var emptyListMessage = SingleLiveEvent<String>()

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

    fun getCenterListData(): LiveData<ArrayList<Center>> {
        return centersList
    }

    fun getEmptyListMessage():LiveData<String>{
        return emptyListMessage
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
                    if (t.centers?.size!! > 0) {
                        centersList.setValue(t.centers as ArrayList<Center>)
                    }else{
                        emptyListMessage.setValue("No centers available")
                    }
                }

                override fun onError(e: Throwable) {
                    showProgress.value = false
                    errorMessage.value = e
                }

            })
    }

    fun getAppointmentByPinCodeAndDate(pinCode:Int ,date: String){
        repository.getAppointmentByPinCodeAndDate(pinCode, date,
            object : SingleObserver<AppointmentResponse> {
                override fun onSubscribe(d: Disposable) {
                    showProgress.value = true
                    compositeDisposable?.add(d)
                }

                override fun onSuccess(t: AppointmentResponse) {
                    showProgress.value = false
                    if (t.centers?.size!! > 0) {
                        centersList.setValue(t.centers as java.util.ArrayList<Center>)
                    }else{
                        emptyListMessage.setValue("No centers available")
                    }

                }

                override fun onError(e: Throwable) {
                    showProgress.value = false
                    errorMessage.value = e
                }
            })
    }
}