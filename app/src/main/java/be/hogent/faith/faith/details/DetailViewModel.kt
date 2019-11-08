package be.hogent.faith.faith.details

import androidx.lifecycle.LiveData
import be.hogent.faith.domain.models.detail.Detail

interface DetailViewModel<T : Detail> {
    /**
     * Updates with a new version of the [Detail] every time it is saved.
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
    fun onSaveClicked()
}