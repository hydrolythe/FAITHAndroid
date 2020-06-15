package be.hogent.faith.service.usecases.detail.drawingDetail

import android.graphics.Bitmap
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.service.repositories.ITemporaryFileStorageRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.util.ThumbnailProvider
import io.reactivex.Completable
import io.reactivex.Scheduler

/**
 * Use case to be used when the bitmap inside an existing [DrawingDetail] should be overwritten
 * with a new bitmap. This means the location of the file in the [DrawingDetail] is not changed.
 */
class OverwriteDrawingDetailUseCase(
    private val storageRepo: ITemporaryFileStorageRepository,
    private val thumbnailProvider: ThumbnailProvider,
    observer: Scheduler
) : CompletableUseCase<OverwriteDrawingDetailUseCase.Params>(observer) {

    override fun buildUseCaseObservable(params: Params): Completable {
        return storageRepo.overwriteExistingDrawingDetail(params.bitmap, params.detail)
            .andThen(Completable.fromAction {
                params.detail.thumbnail = thumbnailProvider.getBase64EncodedThumbnail(params.bitmap)
            })
    }

    data class Params(
        val bitmap: Bitmap,
        val detail: DrawingDetail
    )
}