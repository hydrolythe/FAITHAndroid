package be.hogent.faith.faith.details.text.create

import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.details.DetailViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.detail.textDetail.CreateTextDetailUseCase
import be.hogent.faith.service.usecases.detail.textDetail.LoadTextDetailUseCase
import be.hogent.faith.service.usecases.detail.textDetail.OverwriteTextDetailUseCase
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.threeten.bp.LocalDateTime
import timber.log.Timber

class TextDetailViewModel(
    private val loadTextDetailUseCase: LoadTextDetailUseCase,
    private val createTextDetailUseCase: CreateTextDetailUseCase,
    private val overwriteTextDetailUseCase: OverwriteTextDetailUseCase
) : ViewModel(), DetailViewModel<TextDetail> {

    private val _savedDetail = MutableLiveData<TextDetail>()
    override val savedDetail: LiveData<TextDetail> = _savedDetail

    private val _getDetailMetaData = SingleLiveEvent<Unit>()
    override val getDetailMetaData: LiveData<Unit> = _getDetailMetaData

    override fun loadExistingDetail(existingDetail: TextDetail) {
        _existingDetail = existingDetail
        val params = LoadTextDetailUseCase.LoadTextParams(existingDetail)
        loadTextDetailUseCase.execute(params, LoadTextUseCaseHandler())
    }

    private val _text = MutableLiveData<String>()
    /**
     * The text being written into the editor. Will change over time as the user writes.
     */
    val text: LiveData<String> = _text

    private val _initialText = MutableLiveData<String>()
    /**
     * Will be updated with the text from an existing detail once it is loaded.
     * May be used to place this initial text in an editor when opening the existing detail.
     * Will not change afterwards.
     */
    val initialText: LiveData<String> = _initialText

    private var _existingDetail: TextDetail? = null

    protected val _selectedTextColor = MutableLiveData<@ColorInt Int>()
    val selectedTextColor: LiveData<Int>
        get() = _selectedTextColor

    protected val _customTextColor = MutableLiveData<@ColorInt Int>()
    val customTextColor: LiveData<Int>
        get() = _customTextColor

    private val _selectedFontSize = MutableLiveData<FontSize>()
    val selectedFontSize: LiveData<FontSize> = _selectedFontSize

    // als je dit initieel een waarde geeft dan komt de tekst al in bold te staan
    private val _boldClicked = MutableLiveData<Boolean?>()
    val boldClicked: LiveData<Boolean?> = _boldClicked

    private val _italicClicked = MutableLiveData<Boolean?>()
    val italicClicked: LiveData<Boolean?> = _italicClicked

    private val _underlineClicked = MutableLiveData<Boolean?>()
    val underlineClicked: LiveData<Boolean?> = _underlineClicked

    private val _fontsizeClicked = MutableLiveData<Boolean>()
    val fontsizeClicked: LiveData<Boolean> = _fontsizeClicked

    private val _customTextColorClicked = SingleLiveEvent<Unit>()
    val customTextColorClicked: LiveData<Unit>
        get() = _customTextColorClicked

    private val _cancelClicked = SingleLiveEvent<Unit>()
    val cancelClicked: LiveData<Unit>
        get() = _cancelClicked

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int> = _errorMessage

    val customTextColorSelected: LiveData<Boolean> =
        Transformations.map<Int, Boolean>(_selectedTextColor) { color ->
            color == _customTextColor.value
        }

    init {
        _customTextColor.value = R.color.green
        _fontsizeClicked.value = false
        _selectedFontSize.value =
            FontSize.NORMAL
        // Start with empty String so contents are never null
        _text.value = ""
    }

    fun onBoldClicked() {
        _boldClicked.value =
            if (_boldClicked.value != null && _boldClicked.value == true) false else true
    }

    fun onItalicClicked() {
        _italicClicked.value =
            if (_italicClicked.value != null && _italicClicked.value == true) false else true
    }

    fun onUnderlineClicked() {
        _underlineClicked.value =
            if (_underlineClicked.value != null && _underlineClicked.value == true) false else true
    }

    fun onCustomTextColorClicked() {
        _customTextColorClicked.call()
        _selectedTextColor.value = _customTextColor.value
    }

    fun pickFontsizeClicked() {
        _fontsizeClicked.value = true
    }

    fun pickTextColor(@ColorInt color: Int) {
        _selectedTextColor.value = color
    }

    fun pickCustomTextColor(@ColorInt color: Int) {
        _customTextColor.value = color
        _selectedTextColor.value = color
    }

    fun pickFontSize(fontSize: FontSize) {
        _selectedFontSize.value = fontSize
        _fontsizeClicked.value = false
    }

    fun setText(text: String) {
        _text.value = text
    }

    fun onCancelClicked() {
        _cancelClicked.call()
    }

    override fun onSaveClicked() {
        if (!_text.value.isNullOrEmpty()) {
            if (_existingDetail == null) {
                val params = CreateTextDetailUseCase.Params(text.value!!)
                createTextDetailUseCase.execute(params, CreateTextDetailUseCaseHandler())
            } else {
                val params =
                    OverwriteTextDetailUseCase.Params(_text.value!!, _existingDetail!!)
                overwriteTextDetailUseCase.execute(params, OverwriteTextDetailUseCaseHandler())
            }
        } else {
            _errorMessage.postValue(R.string.text_save_error_emptyinput)
        }
    }

    private inner class OverwriteTextDetailUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _savedDetail.value = _existingDetail
        }

        override fun onError(e: Throwable) {
            Timber.e(e)
            _errorMessage.postValue(R.string.error_save_text_failed)
        }
    }

    private inner class CreateTextDetailUseCaseHandler : DisposableSingleObserver<TextDetail>() {
        override fun onSuccess(createdDetail: TextDetail) {
            _existingDetail = createdDetail
            _getDetailMetaData.call()
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
            _errorMessage.postValue(R.string.error_ophalen_textdetail)
        }
    }

    override fun setDetailsMetaData(title: String, dateTime: LocalDateTime) {
        _existingDetail?.let {
            it.title = title
            it.dateTime = dateTime
        }
        _savedDetail.value = _existingDetail
    }
}
