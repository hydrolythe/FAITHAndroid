package be.hogent.faith.faith.details.video.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.detail.Detail
import java.io.File

class ViewVideoViewModel : ViewModel() {

    private var _currentFile = MutableLiveData<File>()
    val currentFile: LiveData<File>
        get() = _currentFile

    fun loadExistingDetail(existingDetail: Detail) {
        _currentFile.value = existingDetail.file
    }
}