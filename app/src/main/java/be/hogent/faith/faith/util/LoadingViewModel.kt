package be.hogent.faith.faith.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class LoadingViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>().apply { value = false }
    val isLoading: LiveData<Boolean> = _isLoading

    fun startLoading() {
        _isLoading.value = true
    }

    fun doneLoading() {
        _isLoading.value = false
    }
}
