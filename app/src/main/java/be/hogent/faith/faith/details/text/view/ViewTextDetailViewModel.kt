package be.hogent.faith.faith.details.text.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.hogent.faith.R
import be.hogent.faith.faith.models.detail.TextDetail
import be.hogent.faith.faith.details.ViewDetailBaseViewModel
import be.hogent.faith.faith.details.text.create.ITextDetailRepository
import be.hogent.faith.faith.details.text.create.TextDetailRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class ViewTextDetailViewModel(val textDetailRepository: ITextDetailRepository) : ViewDetailBaseViewModel<TextDetail>() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _initialText = MutableLiveData<String>()
    val initialText: LiveData<String> = _initialText

    override fun setDetail(detail: TextDetail) {
        super.setDetail(detail)
        uiScope.launch {
            val rewrittenFile = detail.file.absolutePath.replace("_encrypted", "")
            detail.file = File(rewrittenFile)
            val result = textDetailRepository.loadDetail(detail)
            if(result.success!=null){
                _initialText.value = result.success.token
            }
            if(result.exception!=null){
                Timber.e(result.exception)
                _errorMessage.postValue(R.string.error_load_text_failed)
            }
        }
    }
}
