package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Detail
import be.hogent.faith.domain.models.DetailType
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.io.File

class TakeEventPhotoUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<TakeEventPhotoUseCase.Params>(
    Schedulers.io(),
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return Completable.fromSingle(
            storageRepository.movePhotoFromTempStorage(params.tempPhotoFile, params.event, params.photoName)
                .doOnSuccess { storedFile ->
                    //TODO: add name once this property is added to Detail
                    params.event.addDetail(Detail(DetailType.PICTURE, storedFile))
                }
        )
    }

    data class Params(val tempPhotoFile: File, val event: Event, val photoName: String)
}