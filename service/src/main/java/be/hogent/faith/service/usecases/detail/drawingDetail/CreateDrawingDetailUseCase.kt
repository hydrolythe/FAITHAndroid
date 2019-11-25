package be.hogent.faith.service.usecases.detail.drawingDetail

import android.graphics.Bitmap
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.service.usecases.base.SingleUseCase
import be.hogent.faith.storage.localStorage.ITemporaryStorage
import io.reactivex.Scheduler
import io.reactivex.Single

class CreateDrawingDetailUseCase(
    private val storageRepository: ITemporaryStorage,
    observeScheduler: Scheduler
) : SingleUseCase<DrawingDetail, CreateDrawingDetailUseCase.Params>(observeScheduler) {

    override fun buildUseCaseSingle(params: Params): Single<DrawingDetail> {
        return storageRepository.storeBitmapTemporarily(params.bitmap)
            .map { storedFile -> DrawingDetail(storedFile) }
    }

    class Params(
        val bitmap: Bitmap
    )
}