package be.hogent.faith.faith.backpackScreen.externalFile

import android.net.Uri
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.faith.details.DetailViewModel
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.detail.photoDetail.CreatePhotoDetailUseCase
import io.reactivex.observers.DisposableSingleObserver
import timber.log.Timber
import java.io.File



class ExternalFileViewModel(private val createPhotoDetailUseCase: CreatePhotoDetailUseCase) : ViewModel(), DetailViewModel<PhotoDetail> {


    private val _cancelClicked = SingleLiveEvent<Unit>()
    val cancelClicked: LiveData<Unit>
        get() = _cancelClicked

    private val _savedDetail = MutableLiveData<PhotoDetail>()
    override val savedDetail: LiveData<PhotoDetail> = _savedDetail

    private var navigation: TakePhotoFragment.PhotoScreenNavigation? = null

    private var _currentFile = MutableLiveData<File>()
    val currentFile: LiveData<File>
        get() = _currentFile

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    fun setCurrentFile(uri: Uri?) {

        _currentFile.value = File(uri!!.path)
    }

    fun onCancelClicked() {
        _cancelClicked.call()
    }


    override fun loadExistingDetail(existingDetail: PhotoDetail) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onSaveClicked() {
        require(_currentFile.value != null)
        val params = CreatePhotoDetailUseCase.Params(_currentFile.value!!)
        createPhotoDetailUseCase.execute(params, CreatePhotoDetailUseCaseHandler())


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

}