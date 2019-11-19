package be.hogent.faith.service.usecases.event

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.StorageRepository
import be.hogent.faith.storage.localStorage.TemporaryStorageInterface
import io.reactivex.Completable
import io.reactivex.Scheduler

/**
 * Stores the (colored) emotion avatarName for an event on the device's storage.
 * Will overwrite a previously stored avatarName.
 */
class SaveEmotionAvatarUseCase(
    private val storageRepository: TemporaryStorageInterface,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEmotionAvatarUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return Completable.fromSingle(
            storageRepository.storeBitmapTemporarily(
                params.bitmap
            ).doOnSuccess {
                params.event.emotionAvatar = it
            }
        )
    }

    data class Params(val bitmap: Bitmap, val event: Event)
}