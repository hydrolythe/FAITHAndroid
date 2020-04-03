package be.hogent.faith.faith

import android.graphics.Bitmap
import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.storage.local.ITemporaryFileStorageRepository
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.FileFactory
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

class TestITemporyStorage : ITemporaryFileStorageRepository {
    override fun storeTextTemporarily(text: String): Single<File> {
        return Single.just(FileFactory.makeFile())
    }

    override fun storeBitmapTemporarily(bitmap: Bitmap): Single<File> {
        return Single.just(FileFactory.makeFile())
    }

    override fun overwriteExistingDrawingDetail(
        bitmap: Bitmap,
        drawingDetail: DrawingDetail
    ): Completable {
        return Completable.complete()
    }

    override fun overwriteTextDetail(text: String, existingDetail: TextDetail): Completable {
        return Completable.complete()
    }

    override fun saveDetailFileWithContainer(
        container: DetailsContainer,
        detail: Detail
    ): Completable {
        return Completable.complete()
    }

    override fun loadTextFromExistingDetail(textDetail: TextDetail): Single<String> {
        return Single.just(DataFactory.randomString())
    }
}