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
    private val event: Event
) : ViewModel() {

    val photoName = MutableLiveData<String>()

    /**
     * The file where the photo is temporarily saved.
     * Will be filled in by the [TakePhotoFragment] once the picture was taken.
     */
    lateinit var tempPhotoFile: File

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
                _photoSavedSuccessFully.value = Unit
                // Name has to be cleared so future pictures start with an empty name
                photoName.value = ""
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