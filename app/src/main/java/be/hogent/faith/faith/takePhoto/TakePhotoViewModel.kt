package be.hogent.faith.faith.takePhoto

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.TakeEventPhotoUseCase
import io.reactivex.disposables.CompositeDisposable
import java.io.File

class TakePhotoViewModel(
    private val takeEventPhotoUseCase: TakeEventPhotoUseCase,
    private val tempPhotoFile: File,
    private val event: Event
) : ViewModel() {

   val photoName = MutableLiveData<String>()

    private val _takePhotoButtonClicked = SingleLiveEvent<Unit>()
    val takePhotoButtonClicked: LiveData<Unit>
        get() = _takePhotoButtonClicked

    private val _emotionAvatarButtonClicked = SingleLiveEvent<Unit>()
    val emotionAvatarButtonClicked: LiveData<Unit>
        get() = _emotionAvatarButtonClicked

    private val _cancelButtonClicked = SingleLiveEvent<Unit>()
    val cancelButtonClicked: LiveData<Unit>
        get() = _cancelButtonClicked

    private val _photoSavedSuccessFully = SingleLiveEvent<Unit>()
    val photoSavedSuccessFully: LiveData<Unit>
        get() = _photoSavedSuccessFully

    private val disposables = CompositeDisposable()

    /**
     * Will be updated with the latest error message when an error occurs when saving the recording.
     */
    private val _recordingSaveFailed = MutableLiveData<String>()
    val recordingSaveFailed: LiveData<String>
        get() = _recordingSaveFailed

    init {
        photoName.value = ""
    }

    fun onTakePhotoButtonClicked() {
        _takePhotoButtonClicked.call()
    }

    fun onGoToEmotionAvatarButtonClicked() {
        _emotionAvatarButtonClicked.call()
    }

    fun onSaveButtonClicked() {
        val disposable = takeEventPhotoUseCase.execute(
            TakeEventPhotoUseCase.Params(tempPhotoFile, event, photoName.value!!)
        ).subscribe(
            {
                _photoSavedSuccessFully.call()
                //TOOD: main screen should listen to this event to update recyclerview
            },
            {
                _recordingSaveFailed.postValue(it.message)
            }
        )
        disposables.add(disposable)
    }

    fun onCancelButtonClicked() {
        _cancelButtonClicked.call()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}