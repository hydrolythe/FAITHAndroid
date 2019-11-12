package be.hogent.faith.faith

import be.hogent.faith.domain.models.detail.Detail

interface DetailFragment<T : Detail> {
    var detailFinishedListener: DetailFinishedListener
}

interface DetailFinishedListener {
    fun onDetailFinished(detail: Detail)
}