package be.hogent.faith.faith.details

import be.hogent.faith.domain.models.detail.Detail

/**
 * Defines what a Fragment for creating and editing of [Detail]s should provide.
 *
 */
interface DetailFragment<T : Detail> {
    var detailFinishedListener: DetailFinishedListener
}

interface DetailFinishedListener {
    /**
     * Will be called when the user has finished creating or editing a detail.
     * When the user wants to edit a new detail, it will get back the same detail object, but the
     * actual contents in the [Detail]'s file might have been changed.
     */
    fun onDetailFinished(detail: Detail)
}