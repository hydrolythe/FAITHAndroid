package be.hogent.faith.service.usecases.event

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.repositories.ITemporaryFileStorageRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler

/**
 * Stores the (colored) emotion avatarName for an event on the device's storage.
 * Will overwrite a previously stored avatarName.
 */
class SaveEmotionAvatarUseCase(
    private val tempStorageRepo: ITemporaryFileStorageRepository,
    observer: Scheduler
) : CompletableUseCase<SaveEmotionAvatarUseCase.Params>(
    observer
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return Completable.fromSingle(
            tempStorageRepo.storeBitmap(
                params.bitmap
            ).doOnSuccess {
                params.event.emotionAvatar = it
            }
        )
    }

    data class Params(val bitmap: Bitmap, val event: Event)
}