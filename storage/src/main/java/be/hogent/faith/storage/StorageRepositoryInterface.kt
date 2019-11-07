package be.hogent.faith.storage

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.TextDetail
import io.reactivex.Single
import java.io.File

interface StorageRepositoryInterface {

    fun storeTextDetatil(text: String): Single<File>
    fun storeBitmap(bitmap: Bitmap): Single<File>

    fun storeTextDetailWithEvent(textDetail: TextDetail, event: Event)
    fun storeDrawingDetailWithEvent(drawingDetail: DrawingDetail, event: Event)
}