package be.hogent.faith.faith.details.externalFile

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.VideoDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.faith.details.DetailViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.detail.externalVideo.CreateVideoDetailUseCase
import be.hogent.faith.service.usecases.detail.photoDetail.CreatePhotoDetailUseCase
import io.reactivex.observers.DisposableSingleObserver
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

    private val _savedDetail = MutableLiveData<Detail>()
    override val savedDetail: LiveData<Detail>
        get() = _savedDetail

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

    override fun loadExistingDetail(existingDetail: Detail) {
        throw UnsupportedOperationException("This viewmodel can't open details")
    }

    override fun onSaveClicked() {
        require(_currentFile.value != null)
        val file = File(_currentFile.value!!.path)

        when (file.path.toLowerCase(Locale.ROOT).substring(
            file.path.toLowerCase(Locale.ROOT).lastIndexOf(
                "."
            )
        )) {
            ".png" -> {
                val params = CreatePhotoDetailUseCase.Params(_currentFile.value!!)
                createPhotoDetailUseCase.execute(
                    params,
                    object : DisposableSingleObserver<PhotoDetail>() {
                        override fun onSuccess(createdDetail: PhotoDetail) {
                            _savedDetail.postValue(createdDetail)
                        }

                        override fun onError(e: Throwable) {
                            _errorMessage.postValue(R.string.create_photo_failed)
                            Timber.e(e)
                        }
                    })
            }
            ".mp4" -> {
                val params = CreateVideoDetailUseCase.Params(_currentFile.value!!)
                createVideoDetailUseCase.execute(
                    params,
                    object : DisposableSingleObserver<VideoDetail>() {
                        override fun onSuccess(createdDetail: VideoDetail) {
                            _savedDetail.postValue(createdDetail)
                        }

                        override fun onError(e: Throwable) {
                            _errorMessage.postValue(R.string.create_video_failed)
                            Timber.e(e)
                        }
                    })
            }
            else -> _errorMessage.postValue(R.string.unauthorized_file_type)
        }
    }
}
