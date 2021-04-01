package be.hogent.faith.faith.details.externalFile

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.models.detail.Detail
import be.hogent.faith.faith.details.DetailViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import org.threeten.bp.LocalDateTime
import java.io.File

class ExternalFileViewModel() : ViewModel(), DetailViewModel<Detail> {

    private val _cancelClicked = SingleLiveEvent<Unit>()
    val cancelClicked: LiveData<Unit>
        get() = _cancelClicked

    private val _getDetailMetaData = SingleLiveEvent<Unit>()
    override val getDetailMetaData: LiveData<Unit> = _getDetailMetaData

    private var _existingDetail: Detail? = null

    private val _savedDetail = MutableLiveData<Detail>()
    override val savedDetail: LiveData<Detail> = _savedDetail

    private var currentFileWithType: Pair<File, ExternalFileType>? = null

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    fun setCurrentFile(file: File, fileType: ExternalFileType) {
        currentFileWithType = Pair(file, fileType)
    }

    fun onCancelClicked() {
        _cancelClicked.call()
    }

    override fun loadExistingDetail(existingDetail: Detail) {
        throw UnsupportedOperationException("This viewmodel can't open existing details")
    }

    override fun onSaveClicked() {
        require(currentFileWithType != null)
        when (currentFileWithType?.second) {
            ExternalFileType.PICTURE -> saveExternalPhoto()
            ExternalFileType.VIDEO -> saveExternalVideo()
        }
    }

    private fun saveExternalVideo() {

    }

    private fun saveExternalPhoto() {

    }

    override fun setDetailsMetaData(title: String, dateTime: LocalDateTime) {
        _existingDetail?.let {
            it.title = title
            it.dateTime = dateTime
        }
        _savedDetail.value = _existingDetail
    }

    enum class ExternalFileType {
        VIDEO, PICTURE
    }
}
