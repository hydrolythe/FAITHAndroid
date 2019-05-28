package be.hogent.faith.faith.emotionCapture.takePhoto

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.util.TAG
import java.io.File

class TakePhotoViewModel : ViewModel() {

    private var _currentState = MutableLiveData<PhotoState>()

    val _photo = MutableLiveData<File>()
    val photo: LiveData<File>
        get() = _photo

    internal val _takePhotoButtonClicked = SingleLiveEvent<Unit>()
    val takePhotoButtonClicked: LiveData<Unit>
        get() = _takePhotoButtonClicked

    internal val _okPhotoButtonClicked = SingleLiveEvent<Unit>()
    val okPhotoButtonClicked: LiveData<Unit>
        get() = _okPhotoButtonClicked

    internal val _notOkPhotoButtonClicked = SingleLiveEvent<Unit>()
    val notOkPhotoButtonClicked: LiveData<Unit>
        get() = _notOkPhotoButtonClicked

    val visibilityTakePhoto = Transformations.map<PhotoState, Int>(_currentState) { state ->
        Log.d(TAG, "state $state")
        if (state is TakePhotoState) View.VISIBLE else View.GONE
    }

    val visibilityPhotoTakenNotSaved = Transformations.map<PhotoState, Int>(_currentState) { state ->
        if (state is PhotoTakenState) View.VISIBLE else View.GONE
    }

    val visibilityPhotoTakenOrSaved = Transformations.map<PhotoState, Int>(_currentState) { state ->
        if (state is PhotoTakenState || state is PhotoSavedState) View.VISIBLE else View.GONE
    }

    init {
        _currentState.value = TakePhotoState()
    }

    internal fun setState(state: PhotoState) {
        _currentState.value = state
    }

    fun onTakePhotoButtonClicked() {
        _currentState.value?.takePhoto(this)
    }

    fun onOkPhotoButtonClicked() {
        _currentState.value?.savePhoto(this)
    }

    fun onNotOkPhotoButtonClicked() {
        _currentState.value?.deletePhoto(this)
    }

    fun setPhotoInCache(file: File) {
        _currentState.value?.setPhotoInCache(this, file)
    }

    fun setSavedPhoto(file: File) {
        _currentState.value?.setSavedPhoto(this, file)
    }
}

internal abstract class PhotoState {
    open fun takePhoto(context: TakePhotoViewModel) {
        Log.e(TAG, "Take photo not allowed")
    }

    open fun setPhotoInCache(context: TakePhotoViewModel, file: File) {
        Log.e(TAG, "Save photo in cache not allowed")
    }

    open fun savePhoto(context: TakePhotoViewModel) {
        Log.e(TAG, "Save photo not allowed")
    }

    open fun setSavedPhoto(context: TakePhotoViewModel, file: File) {
        Log.e(TAG, "Save photo not allowed")
    }

    open fun deletePhoto(context: TakePhotoViewModel) {
        Log.e(TAG, "Delete photo not allowed")
    }
}

internal class TakePhotoState : PhotoState() {
    override fun takePhoto(context: TakePhotoViewModel) {
        context._takePhotoButtonClicked.call()
    }

    override fun setPhotoInCache(context: TakePhotoViewModel, file: File) {
        context._photo.value = file
        context.setState(PhotoTakenState())
    }
}

internal class PhotoTakenState : PhotoState() {

    override fun deletePhoto(context: TakePhotoViewModel) {
        context._notOkPhotoButtonClicked.call()
        context._photo.value = null
        context.setState(TakePhotoState())
    }

    override fun savePhoto(context: TakePhotoViewModel) {
        context._okPhotoButtonClicked.call()
    }

    override fun setSavedPhoto(context: TakePhotoViewModel, file: File) {
        context._photo.value = file
        context.setState(PhotoSavedState())
    }
}

internal class PhotoSavedState : PhotoState()