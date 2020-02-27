package be.hogent.faith.storage

import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import io.reactivex.Completable
import io.reactivex.Flowable
import java.io.File

class DummyStorageRepository() : IDummyStorageRepository{
    override fun getBackpackDetailsUserTestData() : List<Detail> {
        val details = ArrayList<Detail>()
        val file = File("users/oz7czHu2FGdyBTNbZMOe6tCRw612/events/79679ea5-3583-44a8-9d3e-dc98ba3611ba/c8d761dd-f6d0-4603-b53c-52c28c16a20e")
        var audio = AudioDetail(file)
        details.add(audio)
        var draw = DrawingDetail(file)
        details.add(draw)
        var photo = PhotoDetail(file)
        details.add(photo)
        var text = TextDetail(file)
        details.add(text)
        return details
    }

    override fun storeDetail(detail: Detail): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}