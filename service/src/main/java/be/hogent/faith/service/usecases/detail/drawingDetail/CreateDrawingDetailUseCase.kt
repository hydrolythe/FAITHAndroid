package be.hogent.faith.service.usecases.detail.drawingDetail

import android.graphics.Bitmap
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.service.repositories.ITemporaryFileStorageRepository
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single

class CreateDrawingDetailUseCase(
    private val storageRepository: ITemporaryFileStorageRepository,
    observer: Scheduler
) : SingleUseCase<DrawingDetail, CreateDrawingDetailUseCase.Params>(observer) {

    override fun buildUseCaseSingle(params: Params): Single<DrawingDetail> {
        return storageRepository.storeBitmap(params.bitmap)
            // TODO: UUID van detail en degene gebruikt in pad  moeten zelfde zijn
            .map { storedFile -> DrawingDetail(storedFile) }
    }

    class Params(
        val bitmap: Bitmap
    )
}