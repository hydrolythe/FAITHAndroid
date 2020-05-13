package be.hogent.faith.faith.details

import androidx.lifecycle.LiveData
import be.hogent.faith.domain.models.detail.Detail

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
     * Load an existing detail into this ViewModel. This will ensure the contents of the
     * given detail are shown.
     */
    fun loadExistingDetail(existingDetail: T)

    /**
     * Trigger when the user clicks the Save button
     */
    fun onSaveClicked() {}
}