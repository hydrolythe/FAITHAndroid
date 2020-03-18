package be.hogent.faith.faith.backpackScreen.externalFile

import android.net.Uri
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.faith.details.DetailViewModel
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.backpack.CreateExternalVideoDetailUseCase
import be.hogent.faith.service.usecases.detail.photoDetail.CreatePhotoDetailUseCase
import io.reactivex.observers.DisposableSingleObserver
import timber.log.Timber
import java.io.File
import java.util.*


class ExternalFileViewModel(private val createPhotoDetailUseCase: CreatePhotoDetailUseCase,
                            private val createExternalVideoDetailUseCase: CreateExternalVideoDetailUseCase) : ViewModel(), DetailViewModel<Detail> {


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
            val params = CreatePhotoDetailUseCase.Params(file)
            createPhotoDetailUseCase.execute(params, CreatePhotoDetailUseCaseHandler())
        } else {
            val params = CreateExternalVideoDetailUseCase.Params(file)
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


    override fun loadExistingDetail(existingDetail: Detail) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}