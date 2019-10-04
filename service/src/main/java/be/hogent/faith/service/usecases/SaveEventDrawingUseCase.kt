package be.hogent.faith.service.usecases

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.service.usecases.base.SingleUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Scheduler
import io.reactivex.Single

class SaveEventDrawingUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : SingleUseCase<DrawingDetail, SaveEventDrawingUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseSingle(params: Params): Single<DrawingDetail> {
        return if (params.existingDetail != null) {
            storageRepository.overwriteEventDetail(params.bitmap, params.existingDetail)
                .andThen(Single.just(params.existingDetail))
        } else {
            storageRepository.saveEventDrawing(params.bitmap, params.event)
                .map { storedFile -> params.event.addNewDrawingDetail(storedFile) }
        }
    }

    data class Params(
        val bitmap: Bitmap,
        val event: Event,
        val existingDetail: DrawingDetail? = null
    )
}