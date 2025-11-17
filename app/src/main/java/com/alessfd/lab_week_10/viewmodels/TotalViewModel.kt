package com.alessfd.lab_week_10.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TotalViewModel : ViewModel() {

    private val _total = MutableLiveData<Int>()
    val total: LiveData<Int> = _total

    init {
        _total.postValue(0)
    }

    fun incrementTotal() {
        _total.postValue(_total.value?.plus(1))
    }

    fun setTotal(newTotal: Int) {
        _total.postValue(newTotal)
    }

}

