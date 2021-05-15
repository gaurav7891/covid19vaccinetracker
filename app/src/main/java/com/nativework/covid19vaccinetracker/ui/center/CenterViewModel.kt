package com.nativework.covid19vaccinetracker.ui.center

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nativework.covid19vaccinetracker.base.BaseViewModel

class CenterViewModel : BaseViewModel() {

    private val time = MutableLiveData<Int>()

    fun setNotificationForAppointment(timeInterval: Int?) {
        time.value = timeInterval
    }

    fun setNotificationAlert(): LiveData<Int> {
        return time
    }
}