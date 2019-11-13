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
import be.hogent.faith.service.usecases.textDetail.CreateTextDetailUseCase
import be.hogent.faith.service.usecases.textDetail.LoadTextDetailUseCase
import be.hogent.faith.service.usecases.textDetail.OverwriteTextDetailUseCase
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import timber.log.Timber

class TextDetailViewModel(
    private val loadTextDetailUseCase: LoadTextDetailUseCase,
    private val createTextDetailUseCase: CreateTextDetailUseCase,
    private val overwriteTextDetailUseCase: OverwriteTextDetailUseCase
) : ViewModel(), DetailViewModel<TextDetail> {

    private val _savedDetail = MutableLiveData<TextDetail>()
    override val savedDetail: LiveData<TextDetail> = _savedDetail

    override fun loadExistingDetail(existingDetail: TextDetail) {
        _existingDetail.value = existingDetail
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

    private val _existingDetail = MutableLiveData<TextDetail>()

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
        if (!_text.value.isNullOrEmpty()) {
            if (_existingDetail.value == null) {
                val params = CreateTextDetailUseCase.Params(text.value!!)
                createTextDetailUseCase.execute(params, CreateTextDetailUseCaseHandler())
            } else {
                val params =
                    OverwriteTextDetailUseCase.Params(_text.value!!, _existingDetail.value!!)
                overwriteTextDetailUseCase.execute(params, OverwriteTextDetailUseCaseHandler())
            }
        } else {
            _errorMessage.postValue(R.string.text_save_error_emptyinput)
        }
    }

    private inner class OverwriteTextDetailUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _savedDetail.value = _existingDetail.value
        }

        override fun onError(e: Throwable) {
            Timber.e(e)
            _errorMessage.postValue(R.string.error_save_text_failed)
        }
    }

    private inner class CreateTextDetailUseCaseHandler : DisposableSingleObserver<TextDetail>() {
        override fun onSuccess(createdDetail: TextDetail) {
            _savedDetail.value = createdDetail
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
