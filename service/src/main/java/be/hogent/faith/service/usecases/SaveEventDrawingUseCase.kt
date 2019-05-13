package be.hogent.faith.service.usecases

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

class SaveEventDrawingUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventDrawingUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return Completable.fromSingle(
            storageRepository.saveEventDrawing(
                params.bitmap,
                params.event
            ).doOnSuccess { storedFile ->
                //TODO: remove second param once detail name is removed
                params.event.addNewPictureDetail(storedFile, "Drawing")
            }
        )
    }

    data class Params(
        val bitmap: Bitmap,
        val event: Event
    )
}