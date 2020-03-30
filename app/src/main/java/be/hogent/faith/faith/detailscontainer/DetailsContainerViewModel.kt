package be.hogent.faith.faith.detailscontainer

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.backpackScreen.detailFilters.CombinedDetailFilter
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
import io.reactivex.observers.DisposableCompletableObserver

abstract class DetailsContainerViewModel(
    private val saveDetailsContainerDetailUseCase: SaveDetailsContainerDetailUseCase<Backpack>,
    private val deleteDetailsContainerDetailUseCase: DeleteDetailsContainerDetailUseCase<Backpack>
) : ViewModel() {

    protected var details: List<Detail> = emptyList()

    private val detailFilter = CombinedDetailFilter()

    private val searchString = MutableLiveData<String>()

    val audioFilterEnabled = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    val drawingFilterEnabled = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    val photoFilterEnabled = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    val textFilterEnabled = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    val videoFilterEnabled = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    val externalVideoFilterEnabled = MutableLiveData<Boolean>().apply {
        this.value = false
    }

    val filteredDetails: LiveData<List<Detail>> = MediatorLiveData<List<Detail>>().apply {
        addSource(searchString) { query ->
            detailFilter.titleFilter.searchString = query
            value = detailFilter.filter(details)
        }
        addSource(drawingFilterEnabled) { enabled ->
            detailFilter.hasDrawingDetailFilter.isEnabled = enabled
            value = detailFilter.filter(details)
        }
        addSource(textFilterEnabled) { enabled ->
            detailFilter.hasTextDetailFilter.isEnabled = enabled
            value = detailFilter.filter(details)
        }
        addSource(photoFilterEnabled) { enabled ->
            detailFilter.hasPhotoDetailFilter.isEnabled = enabled
            value = detailFilter.filter(details)
        }
        addSource(audioFilterEnabled) { enabled ->
            detailFilter.hasAudioDetailFilter.isEnabled = enabled
            value = detailFilter.filter(details)
        }
        addSource(externalVideoFilterEnabled) { enabled ->
            detailFilter.hasExternalVideoDetailFilter.isEnabled = enabled
            value = detailFilter.filter(details)
        }
    }

    protected var _currentFile = MutableLiveData<Detail>()
    val currentFile: LiveData<Detail>
        get() = _currentFile

    fun setCurrentFile(detail: Detail?) {
        _currentFile.postValue(detail)
    }

    protected val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    protected fun setErrorMessage(errorMsg: Int) {
        _errorMessage.postValue(errorMsg)
    }

    protected val _goToCityScreen = SingleLiveEvent<Any>()
    val goToCityScreen: LiveData<Any> = _goToCityScreen

    protected val _goToDetail = SingleLiveEvent<Detail>()
    val goToDetail: LiveData<Detail> = _goToDetail

    protected val _infoMessage = SingleLiveEvent<Int>()
    val infoMessage: LiveData<Int> = _infoMessage

    fun onFilterPhotosClicked() {
        photoFilterEnabled.value = photoFilterEnabled.value!!.not()
    }

    fun onFilterTextClicked() {
        textFilterEnabled.value = textFilterEnabled.value!!.not()
    }

    fun onFilterDrawingClicked() {
        drawingFilterEnabled.value = drawingFilterEnabled.value!!.not()
    }

    fun onFilterAudioClicked() {
        audioFilterEnabled.value = audioFilterEnabled.value!!.not()
    }

    fun onFilterVideoClicked() {
        videoFilterEnabled.value = videoFilterEnabled.value!!.not()
    }

    fun onFilterExternalVideoClicked() {
        externalVideoFilterEnabled.value = externalVideoFilterEnabled.value!!.not()
    }

    fun setSearchStringText(text: String) {
        searchString.postValue(text)
    }

    fun goToCityScreen() {
        _goToCityScreen.call()
    }

    fun saveTextDetail(user: User, detail: TextDetail) {
        val params = SaveDetailsContainerDetailUseCase.Params(user, user.backpack, detail)
        saveDetailsContainerDetailUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                _infoMessage.postValue(R.string.save_text_success)
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue(R.string.error_save_text_failed)
            }
        })
    }

    fun saveAudioDetail(user: User, detail: AudioDetail) {
        val params = SaveDetailsContainerDetailUseCase.Params(user, user.backpack, detail)
        saveDetailsContainerDetailUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                _infoMessage.postValue(R.string.save_audio_success)
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue(R.string.error_save_audio_failed)
            }
        })
    }

    fun savePhotoDetail(user: User, detail: PhotoDetail) {
        val params = SaveDetailsContainerDetailUseCase.Params(user, user.backpack, detail)
        saveDetailsContainerDetailUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                _infoMessage.postValue(R.string.save_photo_success)
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue(R.string.error_save_photo_failed)
            }
        })
    }

    fun saveDrawingDetail(user: User, detail: DrawingDetail) {
        val params = SaveDetailsContainerDetailUseCase.Params(user, user.backpack, detail)
        saveDetailsContainerDetailUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                _infoMessage.postValue(R.string.save_drawing_success)
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue(R.string.error_save_drawing_failed)
            }
        })
    }

    fun saveExternalVideoDetail(user: User, detail: ExternalVideoDetail) {
        val params = SaveDetailsContainerDetailUseCase.Params(user, user.backpack, detail)
        saveDetailsContainerDetailUseCase.execute(
                params,
                object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        _infoMessage.postValue(R.string.save_video_success)
                    }

                    override fun onError(e: Throwable) {
                        _errorMessage.postValue(R.string.error_save_external_video_failed)
                    }
                })
    }

    fun goToDetail(detail: Detail) {
        _goToDetail.postValue(detail)
    }

    fun deleteDetail(detail: Detail) {
        val params = DeleteDetailsContainerDetailUseCase.Params(detail)
        deleteDetailsContainerDetailUseCase.execute(params, DeleteBackpackDetailUseCaseHandler())
    }

    private inner class DeleteBackpackDetailUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _infoMessage.postValue(R.string.delete_detail_success)
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_delete_detail_failure)
        }
    }

    override fun onCleared() {
        saveDetailsContainerDetailUseCase.dispose()
        deleteDetailsContainerDetailUseCase.dispose()
        super.onCleared()
    }
}
