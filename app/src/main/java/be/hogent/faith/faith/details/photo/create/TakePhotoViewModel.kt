package be.hogent.faith.faith.details.photo.create

import android.view.View
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.faith.details.DetailViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.detail.photoDetail.CreatePhotoDetailUseCase
import io.reactivex.observers.DisposableSingleObserver
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import java.io.File

class TakePhotoViewModel(
    private val createPhotoDetailUseCase: CreatePhotoDetailUseCase
) : ViewModel(), DetailViewModel<PhotoDetail> {

    private val _savedDetail = MutableLiveData<PhotoDetail>()
    override val savedDetail: LiveData<PhotoDetail> = _savedDetail

    private val _getDetailMetaData = SingleLiveEvent<Unit>()
    override val getDetailMetaData: LiveData<Unit> = _getDetailMetaData

    private var currentDetail: PhotoDetail? = null

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    override fun loadExistingDetail(existingDetail: PhotoDetail) {
        throw NotImplementedError("Use the ReviewPhotoFragment to show existing photoSaveFile details.")
    }

    override fun onSaveClicked() {
        require(_tempPhotoSaveFile.value != null)
        val params = CreatePhotoDetailUseCase.Params(_tempPhotoSaveFile.value!!)
        createPhotoDetailUseCase.execute(params, CreatePhotoDetailUseCaseHandler())
    }

    private inner class CreatePhotoDetailUseCaseHandler :
        DisposableSingleObserver<PhotoDetail>() {
        override fun onSuccess(createdDetail: PhotoDetail) {
            currentDetail = createdDetail
            _getDetailMetaData.call()
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.create_photo_failed)
            Timber.e(e)
        }
    }

    private var _currentState = MutableLiveData<PhotoState>()

    private val _tempPhotoSaveFile = MutableLiveData<File>()
    val photoSaveFile: LiveData<File>
        get() = _tempPhotoSaveFile

    private val _takePhotoButtonClicked = SingleLiveEvent<Unit>()
    val takePhotoButtonClicked: LiveData<Unit>
        get() = _takePhotoButtonClicked

    private val _okPhotoButtonClicked = SingleLiveEvent<Unit>()
    val okPhotoButtonClicked: LiveData<Unit>
        get() = _okPhotoButtonClicked

    private val _notOkPhotoButtonClicked = SingleLiveEvent<Unit>()
    val notOkPhotoButtonClicked: LiveData<Unit>
        get() = _notOkPhotoButtonClicked

    private val _frontSelected = MutableLiveData<Boolean>()
    val frontSelected: LiveData<Boolean>
        get() = _frontSelected

    val visibilityTakePhoto: LiveData<Int> =
        Transformations.map<PhotoState, Int>(_currentState) { state ->
            if (state is TakePhotoState) View.VISIBLE else View.INVISIBLE
        }

    val visibilityPhotoTakenNotSaved: LiveData<Int> =
        Transformations.map<PhotoState, Int>(_currentState) { state ->
            if (state is PhotoTakenState) View.VISIBLE else View.INVISIBLE
        }

    val visibilityPhotoTakenOrSaved: LiveData<Int> =
        Transformations.map<PhotoState, Int>(_currentState) { state ->
            if (state is PhotoTakenState || state is PhotoSavedState) View.VISIBLE else View.INVISIBLE
        }

    private val _cancelClicked = SingleLiveEvent<Unit>()
    val cancelClicked: LiveData<Unit>
        get() = _cancelClicked

    init {
        _currentState.value = TakePhotoState()
        _frontSelected.postValue(false)
    }

    internal fun setState(state: PhotoState) {
        _currentState.value = state
    }

    fun onTakePhotoButtonClicked() {
        _currentState.value!!.takePhoto(this)
    }

    fun onOkPhotoButtonClicked() {
        _currentState.value!!.savePhoto(this)
    }

    fun onNotOkPhotoButtonClicked() {
        _currentState.value!!.deletePhoto(this)
    }

    fun onSelfieButtonClicked() {
        _frontSelected.postValue(!frontSelected.value!!)
    }

    /**
     * Call when a photo was taken.
     *
     * @param file the file where the taken photo was saved
     */
    fun photoTaken(file: File) {
        _currentState.value!!.setPhotoSaveFile(this, file)
    }

    fun onCancelClicked() {
        _cancelClicked.call()
    }

    internal abstract class PhotoState {
        open fun takePhoto(context: TakePhotoViewModel) {
            Timber.e("Take photoSaveFile not allowed")
        }

        open fun setPhotoSaveFile(context: TakePhotoViewModel, file: File) {
            Timber.e("Save photoSaveFile in cache not allowed")
        }

        open fun savePhoto(context: TakePhotoViewModel) {
            Timber.e("Save photoSaveFile not allowed")
        }

        open fun deletePhoto(context: TakePhotoViewModel) {
            Timber.e("Delete photoSaveFile not allowed")
        }
    }

    internal class TakePhotoState : PhotoState() {
        override fun takePhoto(context: TakePhotoViewModel) {
            context._takePhotoButtonClicked.call()
        }

        override fun setPhotoSaveFile(context: TakePhotoViewModel, file: File) {
            context._tempPhotoSaveFile.value = file
            context.setState(PhotoTakenState())
        }
    }

    internal class PhotoTakenState : PhotoState() {

        override fun deletePhoto(context: TakePhotoViewModel) {
            context._notOkPhotoButtonClicked.call()
            context._tempPhotoSaveFile.value = null
            context.setState(TakePhotoState())
        }

        override fun savePhoto(context: TakePhotoViewModel) {
            context._okPhotoButtonClicked.call()
            context.onSaveClicked()
        }
    }

    internal class PhotoSavedState : PhotoState()

    override fun setDetailsMetaData(title: String, dateTime: LocalDateTime) {
        currentDetail?.let {
            it.title = title
            it.dateTime = dateTime
        }
        _savedDetail.value = currentDetail
    }
}
