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

class SaveBitmapUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveBitmapUseCase.SaveBitmapParams>(
    Schedulers.io(),
    observeScheduler
) {
    override fun buildUseCaseObservable(params: SaveBitmapParams): Completable {
        return storageRepository.storeBitmap(params.bitmap, params.event).doOnSuccess {
            params.event.addDetail(Detail(DetailType.DRAWING, it))
        }.ignoreElement()
    }

    data class SaveBitmapParams(val bitmap: Bitmap, val event: Event)
}