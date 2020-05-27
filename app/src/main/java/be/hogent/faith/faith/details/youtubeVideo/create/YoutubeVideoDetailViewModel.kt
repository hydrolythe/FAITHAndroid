package be.hogent.faith.faith.details.youtubeVideo.create

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.details.DetailViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.backpack.GetYoutubeVideosFromSearchUseCase
import io.reactivex.subscribers.DisposableSubscriber
import org.threeten.bp.LocalDateTime
import java.lang.UnsupportedOperationException

class YoutubeVideoDetailViewModel(
    private val getYoutubeVideosFromSearchUseCase: GetYoutubeVideosFromSearchUseCase
) : ViewModel(), DetailViewModel<YoutubeVideoDetail> {

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
    override val savedDetail: LiveData<YoutubeVideoDetail>
        get() = _savedDetail

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    private val _backToBackpack = SingleLiveEvent<Any>()
    val backToBackpack: LiveData<Any>
        get() = _backToBackpack

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
    fun onSearch(searchString: String) {
        val params = GetYoutubeVideosFromSearchUseCase.Params(searchString)
        getYoutubeVideosFromSearchUseCase.execute(params, GetYoutubeSnippetsUseCaseHandler())
    }

    private inner class GetYoutubeSnippetsUseCaseHandler :
        DisposableSubscriber<List<YoutubeVideoDetail>>() {
        override fun onNext(t: List<YoutubeVideoDetail>?) {
            _snippets.postValue(t)
        }

        override fun onComplete() {
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.create_video_failed)
            e.printStackTrace()
        }
    }

    override fun onSaveClicked() {
        saveVideoDetail(_selectedSnippet.value!!)
    }

    // Public for testing purposes
    fun saveVideoDetail(detail: YoutubeVideoDetail) {
        _getDetailMetaData.call()
    }

    fun goBackToBackpack() {
        _backToBackpack.call()
    }

    fun clearSnippetsList() {
        _snippets.postValue(emptyList())
    }

    fun clearSelectedSnippet() {
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

    override fun onCleared() {
        super.onCleared()
        getYoutubeVideosFromSearchUseCase.dispose()
    }

    private val _getDetailMetaData = SingleLiveEvent<Unit>()
    override val getDetailMetaData: LiveData<Unit> = _getDetailMetaData

    override fun loadExistingDetail(existingDetail: YoutubeVideoDetail) {
        throw UnsupportedOperationException("Use the ViewFragments to show existing external file.")
    }

    override fun setDetailsMetaData(title: String, dateTime: LocalDateTime) {
        _selectedSnippet.value?.let {
            it.title = title
            it.dateTime = dateTime
        }
        _savedDetail.value = _selectedSnippet.value
    }
}

enum class ShowPreview {
    INITIAL, SHOW, HIDE
}