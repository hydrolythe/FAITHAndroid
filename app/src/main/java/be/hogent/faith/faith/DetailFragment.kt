package be.hogent.faith.faith

import be.hogent.faith.domain.models.detail.Detail

interface DetailFragment<T : Detail> {
    var detailFinishedListener: DetailFinishedListener<T>?
}

interface DetailFinishedListener<T : Detail> {
    fun onDetailFinished(detail: T)
}