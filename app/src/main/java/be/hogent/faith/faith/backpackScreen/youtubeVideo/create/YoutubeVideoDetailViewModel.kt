package be.hogent.faith.faith.backpackScreen.youtubeVideo.create

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.backpack.GetYoutubeVideosFromSearchUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackVideoDetailUseCase
import io.reactivex.observers.DisposableCompletableObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class YoutubeVideoDetailViewModel (
    private val getYoutubeVideosFromSearchUseCase: GetYoutubeVideosFromSearchUseCase,
    private val saveBackpackVideoDetailUseCase: SaveBackpackVideoDetailUseCase
):  ViewModel(){

    private var _snippets = MutableLiveData<List<YoutubeVideoDetail>>()
    val snippets: LiveData<List<YoutubeVideoDetail>>
        get() = _snippets

    private var _selectedSnippet = MutableLiveData<YoutubeVideoDetail>()
    val selectedSnippet: LiveData<YoutubeVideoDetail>
    get() = _selectedSnippet

    private var _showPreview = MutableLiveData<ShowPreview>()
    val showPreview: LiveData<ShowPreview>
        get() = _showPreview

    private var _savedDetail = MutableLiveData<YoutubeVideoDetail>()
    val savedDetail: LiveData<YoutubeVideoDetail>
        get() = _savedDetail

    private var _currentUser = MutableLiveData<User>()

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    private val _videoIsSaved = SingleLiveEvent<Detail>()
    val videoIsSaved: LiveData<Detail>
        get() = _videoIsSaved

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    /**
     * When the fragment starts, preview screen should be hidden
     */
    init {
        _showPreview.postValue(ShowPreview.INITIAL)
    }

    /**
     * Sends a search request to the YouTube Data API
     * checks if the request was successful
     * if successful --> updates the list of video snippets
     */
    fun onSearch(searchString : String){
        uiScope.launch {
            val dataResult = getYoutubeVideosFromSearchUseCase.getYoutubeVideos(searchString)
            if (dataResult is ApiResult.Success<List<YoutubeVideoDetail>>)
            _snippets.postValue(dataResult.data)
        }
    }

    fun onSaveClicked(){
        saveVideoDetail(_selectedSnippet.value!!, _currentUser.value!!)
    }

    //Public for testing purposes
    fun saveVideoDetail(detail: YoutubeVideoDetail, user: User){
        if(detail.fileName.length > 29)
            detail.fileName = detail.fileName.substring(0, 29)
        val params = SaveBackpackVideoDetailUseCase.Params(user, detail)
        saveBackpackVideoDetailUseCase.execute(params, SaveVideoDetailUseCaseHandler())
    }

    private inner class SaveVideoDetailUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _videoIsSaved.postValue(_selectedSnippet.value)
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_audio_failed)
        }
    }

    fun clearSnippetsList() {
        _snippets.postValue(emptyList())
    }

    fun clearSelectedSnippet(){
        _selectedSnippet.postValue(null)
    }

    fun setSelectedSnippet(snippet: YoutubeVideoDetail) {
        _selectedSnippet.postValue(snippet)
    }

    fun showPreview() {
        _showPreview.postValue(ShowPreview.SHOW)
    }

    fun hidePreview() {
        _showPreview.postValue(ShowPreview.HIDE)
    }

    fun setCurrentUser(user: User){
        _currentUser.postValue(user)
    }

    override fun onCleared() {
        super.onCleared()
        saveBackpackVideoDetailUseCase.dispose()
    }
}

enum class ShowPreview {
    INITIAL, SHOW, HIDE
}