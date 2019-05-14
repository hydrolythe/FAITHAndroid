package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler
import java.io.File

class SaveEventPhotoUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventPhotoUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return Completable.fromSingle(
            storageRepository.saveEventPhoto(
                params.tempPhotoFile, params.event
            ).doOnSuccess { storedFile ->
                params.event.addNewPictureDetail(storedFile, params.photoName)
            }
        )
    }

    data class Params(
        val tempPhotoFile: File,
        val event: Event,
        val photoName: String
    )
}