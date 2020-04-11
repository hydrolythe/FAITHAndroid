package be.hogent.faith.service.repositories

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.TextDetail
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

/**
 * The temporary storage is to be used when constructing something (eg. an Event), but the user
 * is not finished yet. As such these files should be saved in the cache.
 * Once the user is finished (and saves the event), these files can be moved to permanent storage
 * using an [IFileStorageRepository].
 */
interface ITemporaryFileStorageRepository {

    /**
     * Stores text the device's cache directory
     */
    fun storeText(text: String): Single<File>

    /**
     * Stores a bitmap in the device's cache directory
     */
    fun storeBitmap(bitmap: Bitmap): Single<File>

    fun overwriteExistingDrawingDetail(bitmap: Bitmap, drawingDetail: DrawingDetail): Completable
    fun overwriteTextDetail(text: String, existingDetail: TextDetail): Completable

    fun storeDetailWithEvent(detail: Detail, event: Event): Completable

    fun loadTextFromExistingDetail(textDetail: TextDetail): Single<String>
}