package be.hogent.faith.faith.details.text.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.details.ViewDetailBaseViewModel
import be.hogent.faith.service.usecases.detail.textDetail.LoadTextDetailUseCase
import timber.log.Timber

class ViewTextDetailViewModel(
    private val loadTextDetailUseCase: LoadTextDetailUseCase
) : ViewDetailBaseViewModel<TextDetail>() {

    private val _initialText = MutableLiveData<String>()
    val initialText: LiveData<String> = _initialText

    override fun setDetail(detail: TextDetail) {
        super.setDetail(detail)
        val params = LoadTextDetailUseCase.LoadTextParams(detail)
        loadTextDetailUseCase.execute(params, LoadTextUseCaseHandler())
    }

    private inner class LoadTextUseCaseHandler : DisposableSingleObserver<String>() {
        override fun onSuccess(loadedString: String) {
            _initialText.value = loadedString
        }

        override fun onError(e: Throwable) {
            Timber.e(e)
            _errorMessage.postValue(R.string.error_load_text_failed)
        }
    }
}
