package be.hogent.faith.storage

import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import io.reactivex.Completable
import io.reactivex.Flowable
import java.io.File

class DummyStorageRepository : IDummyStorageRepository{
    override fun getBackpackDetailsUserTestData(): List<Detail> {
        val details = ArrayList<Detail>()
        val file = File("")
        var audio = AudioDetail(file)
        details.add(audio)
        var draw = DrawingDetail(file)
        details.add(draw)
        var photo = PhotoDetail(file)
        details.add(draw)
        var text = TextDetail(file)
        details.add(text)
        return details
    }

    override fun storeDetail(detail: Detail): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}