package be.hogent.faith.faith.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.faith.util.SingleLiveEvent
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

abstract class DetailsMetaDataViewModel : ViewModel() {

    private val _detailMetaDataSet = SingleLiveEvent<Unit>()
    val detailMetaDataSet: LiveData<Unit> = _detailMetaDataSet

    private val _cancelClicked = SingleLiveEvent<Unit>()
    val cancelClicked: LiveData<Unit> = _cancelClicked

    /**
     * Setting the save dialog header
     */
    val header = MutableLiveData<String>().apply {
        this.value = "Opslaan"
    }

    /**
     * Setting the title
     */
    val detailTitle = MutableLiveData<String>().apply {
        this.value = ""
    }
    protected val _detailTitleErrorMessage = MutableLiveData<Int>()
    val detailTitleErrorMessage: LiveData<Int>
        get() = _detailTitleErrorMessage

    /**
     * setting the date
     */
    val detailDate = MutableLiveData<LocalDate>().apply {
        this.value = LocalDate.now()
    }
    val detailDateString: LiveData<String> =
        Transformations.map(detailDate) { date ->
            date.format(DateTimeFormatter.ISO_DATE)
        }
    private val _detailDateButtonClicked = SingleLiveEvent<Unit>()
    val detailDateButtonClicked: LiveData<Unit> = _detailDateButtonClicked

    fun onDateButtonClicked() {
        _detailDateButtonClicked.call()
    }

    val detailNotes = MutableLiveData<String>().apply {
        this.value = ""
    }

    fun onCancelClicked() {
        _cancelClicked.call()
    }

    open fun onSaveDetailsData() {
        if (validateMetaData()) {
            _detailMetaDataSet.call()
        }
    }

    open fun validateMetaData(): Boolean {
        val title = detailTitle.value!!
        if (title.isBlank()) {
            _detailTitleErrorMessage.postValue(R.string.save_detail_emptyString)
            return false
        } else if (!checkMaxCharacters(title)) {
            _detailTitleErrorMessage.postValue(R.string.save_detail_maxChar)
            return false
        }
        return true
    }

    protected fun checkMaxCharacters(title: String): Boolean {
        return title.length <= 30
    }
}

class CinemaDetailsMetaDataViewModel : DetailsMetaDataViewModel()
class BackpackDetailsMetaDataViewModel : DetailsMetaDataViewModel()
class TreasureChestDetailsMetaDataViewModel : DetailsMetaDataViewModel()
