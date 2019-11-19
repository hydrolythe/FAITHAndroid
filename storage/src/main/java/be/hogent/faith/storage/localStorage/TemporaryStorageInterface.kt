package be.hogent.faith.storage.localStorage

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

interface TemporaryStorageInterface {

    /**
     * Stores text the device's cache directory
     */
    fun storeTextTemporarily(text: String): Single<File>

    /**
     * Stores a bitmap in the device's cache directory
     */
    fun storeBitmapTemporarily(bitmap: Bitmap): Single<File>

    fun storeTextDetailWithEvent(textDetail: TextDetail, event: Event): Completable
    fun storeDrawingDetailWithEvent(drawingDetail: DrawingDetail, event: Event): Completable
    fun storePhotoDetailWithEvent(photoDetail: PhotoDetail, event: Event): Completable
    fun storeAudioDetailWithEvent(audioDetail: AudioDetail, event: Event): Completable
}