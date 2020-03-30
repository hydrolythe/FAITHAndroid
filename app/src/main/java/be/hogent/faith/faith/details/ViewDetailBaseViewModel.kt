package be.hogent.faith.faith.details

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.util.SingleLiveEvent

abstract class ViewDetailBaseViewModel<T : Detail> : ViewModel() {
    protected val _detail = MutableLiveData<T>()
    val detail: LiveData<T>
        get() = _detail

    protected val _cancelClicked = SingleLiveEvent<Unit>()
    val cancelClicked: LiveData<Unit>
        get() = _cancelClicked

    protected val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int> = _errorMessage

    open fun setDetail(detail: T) {
        _detail.value = detail
    }

    open fun onCancelClicked() {
        _cancelClicked.call()
    }
}