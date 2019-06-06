package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.service.usecases.base.SingleUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import java.io.File

class SaveEventPhotoUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : SingleUseCase<Detail, SaveEventPhotoUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseSingle(params: Params): Single<Detail> {
            return      storageRepository.saveEventPhoto(
                params.tempPhotoFile, params.event
            ).map{ storedFile ->
                params.event.addNewPictureDetail(storedFile, params.photoName)
            }
    }

    data class Params(
        val tempPhotoFile: File,
        val event: Event,
        val photoName: String
    )
}