package be.hogent.faith.faith.details.externalFile

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.VideoDetail
import be.hogent.faith.faith.details.DetailViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.detail.externalVideo.CreateVideoDetailUseCase
import be.hogent.faith.service.usecases.detail.photoDetail.CreatePhotoDetailUseCase
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import java.io.File
import java.util.Locale

class ExternalFileViewModel(
    private val createPhotoDetailUseCase: CreatePhotoDetailUseCase,
    private val createVideoDetailUseCase: CreateVideoDetailUseCase
) : ViewModel(), DetailViewModel<Detail> {

    private val _cancelClicked = SingleLiveEvent<Unit>()
    val cancelClicked: LiveData<Unit>
        get() = _cancelClicked

    private val _getDetailMetaData = SingleLiveEvent<Unit>()
    override val getDetailMetaData: LiveData<Unit> = _getDetailMetaData

    private var _existingDetail: Detail? = null

    private val _savedDetail = MutableLiveData<Detail>()
    override val savedDetail: LiveData<Detail>
        get() = _savedDetail

    private var currentFile: File? = null

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    fun setCurrentFile(file: File) {
        currentFile = file
    }

    fun onCancelClicked() {
        _cancelClicked.call()
    }

    override fun loadExistingDetail(existingDetail: Detail) {
        throw UnsupportedOperationException("This viewmodel can't open existing details")
    }

    override fun onSaveClicked() {
        require(currentFile != null)
        val file = File(currentFile!!.path)

        when (file.path.toLowerCase(Locale.ROOT).substring(
            file.path.toLowerCase(Locale.ROOT).lastIndexOf(".")
        )) {
            ".png" -> saveExternalPhoto()
            ".mp4" -> saveExternalVideo()
            else -> _errorMessage.postValue(R.string.unauthorized_file_type)
        }
    }

    private fun saveExternalVideo() {
        val params = CreateVideoDetailUseCase.Params(currentFile!!)
        createVideoDetailUseCase.execute(params, object : DisposableSingleObserver<VideoDetail>() {
                override fun onSuccess(createdDetail: VideoDetail) {
                    _existingDetail = createdDetail
                    _getDetailMetaData.call()
                }

                override fun onError(e: Throwable) {
                    _errorMessage.postValue(R.string.create_video_failed)
                    Timber.e(e)
                }
            })
    }

    private fun saveExternalPhoto() {
        val params = CreatePhotoDetailUseCase.Params(currentFile!!)
        createPhotoDetailUseCase.execute(params, object : DisposableSingleObserver<PhotoDetail>() {
                override fun onSuccess(createdDetail: PhotoDetail) {
                    _existingDetail = createdDetail
                    _getDetailMetaData.call()
                }

                override fun onError(e: Throwable) {
                    _errorMessage.postValue(R.string.create_photo_failed)
                    Timber.e(e)
                }
            })
    }

    override fun setDetailsMetaData(title: String, dateTime: LocalDateTime) {
        _existingDetail?.let {
            it.title = title
            it.dateTime = dateTime
        }
        _savedDetail.value = _existingDetail
    }
}
