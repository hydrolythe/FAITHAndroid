package be.hogent.faith.faith.backpackScreen.externalFile

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.faith.details.DetailViewModel
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.util.SingleLiveEvent
import java.io.File
import java.util.*


class ExternalFileViewModel: ViewModel(), DetailViewModel<Detail> {


    private val _cancelClicked = SingleLiveEvent<Unit>()
    val cancelClicked: LiveData<Unit>
        get() = _cancelClicked

    private val _savedDetail = MutableLiveData<Detail>()
    override val savedDetail: LiveData<Detail>
        get() = _savedDetail


    private var navigation: TakePhotoFragment.PhotoScreenNavigation? = null

    private var _currentFile = MutableLiveData<File>()
    val currentFile: LiveData<File>
        get() = _currentFile

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    fun setCurrentFile(file: File) {

        _currentFile.value = file
    }

    fun onCancelClicked() {
        _cancelClicked.call()
    }


    override fun onSaveClicked() {
        require(_currentFile.value != null)
        val file = File(_currentFile.value!!.path)
        if (file.path.toLowerCase(Locale.ROOT).contains("photo")) {
            _savedDetail.value = PhotoDetail(file, "", UUID.randomUUID())
        } else {
            _savedDetail.value = ExternalVideoDetail(file, "", UUID.randomUUID())
        }


    }


    override fun loadExistingDetail(existingDetail: Detail) {
        throw NotImplementedError("Loading from BackpackViewModel")
    }

}

