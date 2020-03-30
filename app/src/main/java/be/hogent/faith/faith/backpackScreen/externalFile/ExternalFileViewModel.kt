package be.hogent.faith.faith.backpackScreen.externalFile

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.detail.externalVideo.CreateExternalVideoDetailUseCase
import be.hogent.faith.service.usecases.detail.photoDetail.CreatePhotoDetailUseCase
import io.reactivex.observers.DisposableSingleObserver
import timber.log.Timber
import java.io.File
import java.util.Locale

class ExternalFileViewModel(
    private val createPhotoDetailUseCase: CreatePhotoDetailUseCase,
    private val createExternalVideoDetailUseCase: CreateExternalVideoDetailUseCase
) : ViewModel() {

    private val _cancelClicked = SingleLiveEvent<Unit>()
    val cancelClicked: LiveData<Unit>
        get() = _cancelClicked

    private val _savedDetail = MutableLiveData<Detail>()
    val savedDetail: LiveData<Detail>
        get() = _savedDetail

    private var navigation: TakePhotoFragment.PhotoScreenNavigation? = null

    private var _currentFile = MutableLiveData<File>()

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    fun setCurrentFile(file: File) {

        _currentFile.value = file
    }

    fun onCancelClicked() {
        _cancelClicked.call()
    }

    fun onSaveClicked() {
        require(_currentFile.value != null)
        val file = File(_currentFile.value!!.path)
        if (file.path.toLowerCase(Locale.ROOT).contains("photo")) {
            val params = CreatePhotoDetailUseCase.Params(_currentFile.value!!)
            createPhotoDetailUseCase.execute(params, CreatePhotoDetailUseCaseHandler())
        } else {

            val params = CreateExternalVideoDetailUseCase.Params(_currentFile.value!!)
            createExternalVideoDetailUseCase.execute(params, CreateExternalVideoDetailUseCaseHandler())
        }
    }

    private inner class CreatePhotoDetailUseCaseHandler :
            DisposableSingleObserver<PhotoDetail>() {
        override fun onSuccess(createdDetail: PhotoDetail) {
            _savedDetail.value = createdDetail
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.create_photo_failed)
            Timber.e(e)
        }
    }
    private inner class CreateExternalVideoDetailUseCaseHandler :
            DisposableSingleObserver<ExternalVideoDetail>() {
        override fun onSuccess(createdDetail: ExternalVideoDetail) {
            _savedDetail.value = createdDetail
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.create_video_failed)
            Timber.e(e)
        }
    }
}
