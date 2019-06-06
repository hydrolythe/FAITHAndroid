package be.hogent.faith.service.usecases

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.usecases.base.SingleUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Scheduler
import io.reactivex.Single

class SaveEventDrawingUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : SingleUseCase<Detail, SaveEventDrawingUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseSingle(params: Params): Single<Detail> {
        return storageRepository.saveEventDrawing(
            params.bitmap,
            params.event
        ).map { storedFile ->
            // TODO: remove second param once detail name is removed
            params.event.addNewPictureDetail(storedFile, "Drawing")
        }
    }

    data class Params(
        val bitmap: Bitmap,
        val event: Event
    )
}