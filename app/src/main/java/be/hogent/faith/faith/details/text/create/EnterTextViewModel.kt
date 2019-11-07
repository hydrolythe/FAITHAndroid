package be.hogent.faith.faith.details.text.create

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.details.DetailViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.LoadTextDetailUseCase
import be.hogent.faith.service.usecases.CreateTextDetailUseCase
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import timber.log.Timber

class EnterTextViewModel(private val loadTextDetailUseCase: LoadTextDetailUseCase) : ViewModel(),
    DetailViewModel<TextDetail> {

    override val detailAvailable: LiveData<TextDetail>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun loadExistingDetail(existingDetail: TextDetail) {
        _existingDetail.value = existingDetail
        val params = LoadTextDetailUseCase.LoadTextParams(existingDetail)
        loadTextDetailUseCase.execute(params, LoadTextUseCaseHandler())
    }

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    private val _initialText = MutableLiveData<String>()
    /**
     * Will be updated with the text from an existing detail once it is loaded.
     * May be used to place this initial text in an editor when opening the existing detail.
     */
    val initialText: LiveData<String> = _initialText

    private val _existingDetail = MutableLiveData<TextDetail>()
    val existingDetail: LiveData<TextDetail> = _existingDetail

    private val _selectedTextColor = MutableLiveData<@ColorInt Int>()
    val selectedTextColor: LiveData<Int> = _selectedTextColor

    private val _selectedFontSize = MutableLiveData<FontSize>()
    val selectedFontSize: LiveData<FontSize> = _selectedFontSize

    private val _boldClicked = MutableLiveData<Boolean?>()
    val boldClicked: LiveData<Boolean?> = _boldClicked

    private val _italicClicked = MutableLiveData<Boolean?>()
    val italicClicked: LiveData<Boolean?> = _italicClicked

    private val _underlineClicked = MutableLiveData<Boolean?>()
    val underlineClicked: LiveData<Boolean?> = _underlineClicked

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int> = _errorMessage

    private val _saveClicked = SingleLiveEvent<Unit>()
    val saveClicked: LiveData<Unit> = _saveClicked

    init {
        _selectedTextColor.value = Color.BLACK
        _selectedFontSize.value =
            FontSize.NORMAL
        // Start with empty String so contents are never null
        _text.value = ""
    }

    fun onBoldClicked() {
        _boldClicked.value = _boldClicked.value?.not()
    }

    fun onItalicClicked() {
        _italicClicked.value = _italicClicked.value?.not()
    }

    fun onUnderlineClicked() {
        _underlineClicked.value = _underlineClicked.value?.not()
    }

    fun pickTextColor(@ColorInt color: Int) {
        _selectedTextColor.value = color
    }

    fun pickFontSize(fontSize: FontSize) {
        _selectedFontSize.value = fontSize
    }

    fun setText(text: String) {
        _text.value = text
    }

    override fun onSaveClicked() {
        val params = CreateTextDetailUseCase.SaveTextParams(event.value!!, text, existingDetail)
        saveEventTextUseCase.execute(params, SaveTextUseCaseHandler())
    }

    /**
     * Saves a text Detail. If an [existingDetail] is given then the contents of that Detail will
     * be overwritten
     */
    fun saveText(text: String, existingDetail: TextDetail? = null) {
    }

    private inner class SaveTextUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _textSavedSuccessFully.value = Unit
        }

        override fun onError(e: Throwable) {
            Timber.e(e)
            _errorMessage.postValue(R.string.error_save_text_failed)
        }
    }


    enum class FontSize(val size: Int) {
        SMALL(3),
        NORMAL(6),
        LARGE(7)
    }

    private inner class LoadTextUseCaseHandler : DisposableSingleObserver<String>() {
        override fun onSuccess(loadedString: String) {
            _initialText.value = loadedString
        }

        override fun onError(e: Throwable) {
            Timber.e(e)
            _errorMessage.postValue(R.string.error_save_text_failed)
        }
    }
}
