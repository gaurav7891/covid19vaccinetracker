package com.nativework.covid19vaccinetracker.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel : ViewModel() {
    var compositeDisposable: CompositeDisposable? = CompositeDisposable()

    var errorMessage = MutableLiveData<Throwable>()

    var showProgress = MutableLiveData<Boolean>()

    var isDataEmpty = MutableLiveData<Boolean>()

    var isAuthFailure = MutableLiveData<Boolean>()

    //added for getting position after deleting
    var positionSingleEventLiveData = SingleLiveEvent<Int>()

    fun onDeletedAtPosition(): SingleLiveEvent<Int> {
        return positionSingleEventLiveData
    }

    override fun onCleared() {
        super.onCleared()
        if (compositeDisposable != null) {
            compositeDisposable?.clear()
            compositeDisposable = null
        }
    }
}