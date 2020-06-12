package be.hogent.faith.service.usecases.detail.drawingDetail

import android.graphics.Bitmap
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.service.repositories.ITemporaryFileStorageRepository
import be.hogent.faith.service.usecases.base.SingleUseCase
import be.hogent.faith.util.ThumbnailProvider
import io.reactivex.Scheduler
import io.reactivex.Single

class CreateDrawingDetailUseCase(
    private val storageRepository: ITemporaryFileStorageRepository,
    private val thumbnailProvider: ThumbnailProvider,
    observer: Scheduler
) : SingleUseCase<DrawingDetail, CreateDrawingDetailUseCase.Params>(observer) {

    override fun buildUseCaseSingle(params: Params): Single<DrawingDetail> {
        return storageRepository.storeBitmap(params.bitmap)
            .map { storedFile ->
                DrawingDetail(
                    file = storedFile,
                    thumbnail = thumbnailProvider.getBase64EncodedThumbnail(params.bitmap)
                )
            }
    }

    class Params(
        val bitmap: Bitmap
    )
}