package be.hogent.faith.service.usecases

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.base.SingleUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File

class SaveBitmapUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : SingleUseCase<File, SaveBitmapUseCase.SaveBitmapParams>(
    Schedulers.io(),
    observeScheduler
) {
    override fun buildUseCaseObservable(params: SaveBitmapParams): Single<File> {
        return storageRepository.storeBitmap(params.bitmap, params.event)
    }

    data class SaveBitmapParams(val bitmap: Bitmap, val event: Event)
}