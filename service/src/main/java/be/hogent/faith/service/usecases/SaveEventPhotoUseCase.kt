package be.hogent.faith.service.usecases

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Detail
import be.hogent.faith.domain.models.DetailType
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.io.File

class SaveEventPhotoUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventPhotoUseCase.Params>(
    Schedulers.io(),
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return Completable.fromSingle(
            storageRepository.storeBitmap(
                params.bitmap,
                params.event,
                params.saveFile.path
            ).doOnSuccess {
                params.event.addDetail(Detail(DetailType.PICTURE, it))
            }
        )
    }

    data class Params(val bitmap: Bitmap, val event: Event, val saveFile: File)
}