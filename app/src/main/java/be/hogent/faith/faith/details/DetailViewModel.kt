package be.hogent.faith.faith.details

import androidx.lifecycle.LiveData
import be.hogent.faith.faith.models.detail.Detail
import org.threeten.bp.LocalDateTime

/**
 * This interface declares what the ViewModel for a Detail should at least provide.
 * Together with the [DetailFragment] it decouples the creation/editing of a [Detail] from the
 * user of the detail (be it an Event, a Backpack,...).
 */
interface DetailViewModel<T : Detail> {
    /**
     * Updates with a new version of the [Detail] every time it is fully saved, meaning not just the
     * file but also possible metadata.
     */
    val savedDetail: LiveData<T>

    /**
     * Trigger when de detailfile is saved and the user has to enter the details meta data
     */
    val getDetailMetaData: LiveData<Unit>

    /**
     * Load an existing detail into this ViewModel. This will ensure the contents of the
     * given detail are shown.
     */
    fun loadExistingDetail(existingDetail: T)

    /**
     * Set the details meta data the user has filled in in a SaveDetailsContainerDetailDialog
     */
    fun setDetailsMetaData(title: String = "", dateTime: LocalDateTime = LocalDateTime.now())
    /**
     * Trigger when the user clicks the Save button
     */
    fun onSaveClicked()
}