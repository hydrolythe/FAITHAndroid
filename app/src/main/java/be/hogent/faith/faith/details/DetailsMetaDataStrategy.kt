package be.hogent.faith.faith.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.faith.util.SingleLiveEvent
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

abstract class DetailsMetaDataStrategy : ViewModel() {

    protected var saveDetailsMetaData: ISaveDetailsMetaData? = null

    protected val _getDetailMetaData = SingleLiveEvent<Unit>()
    val getDetailMetaData: LiveData<Unit> = _getDetailMetaData

    private val _detailMetaDataSet = SingleLiveEvent<Unit>()
    val detailMetaDataSet: LiveData<Unit> = _detailMetaDataSet

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

    open fun getDetailsData(saveMetaData: ISaveDetailsMetaData) {
        saveDetailsMetaData = saveMetaData
    }

    open fun onSaveDetailsData() {
        if (validateMetaData()) {
            _detailMetaDataSet.call()
            saveDetailsMetaData?.setDetailsMetaData(
                detailTitle.value!!,
                detailDate.value!!.atStartOfDay()
            )
        }
    }

    abstract fun validateMetaData(): Boolean

    /**
     * to update the current detail with the entered metadata, the DetailViewModel implements the interface
     */
    interface ISaveDetailsMetaData {
        fun setDetailsMetaData(title: String, dateTime: LocalDateTime)
    }
}

class EventStrategy : DetailsMetaDataStrategy() {

    override fun getDetailsData(saveMetaData: ISaveDetailsMetaData) {
        super.getDetailsData(saveMetaData)
        onSaveDetailsData()
    }

    override fun validateMetaData(): Boolean {
        return true
    }
}

class CinemaStrategy : DetailsMetaDataStrategy() {

    override fun validateMetaData(): Boolean {
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

class BackpackStrategy : DetailsMetaDataStrategy() {
    override fun validateMetaData(): Boolean {
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
