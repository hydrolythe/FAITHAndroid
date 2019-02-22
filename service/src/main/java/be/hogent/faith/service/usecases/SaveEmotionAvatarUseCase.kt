package be.hogent.faith.service.usecases

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

const val EMOTION_AVATAR_FILENAME = "emotionAvatar"

/**
 * Stores the (colored) emotion avatar for an event on the device's storage.
 * Will overwrite a previously stored avatar.
 */
class SaveEmotionAvatarUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEmotionAvatarUseCase.Params>(
    Schedulers.io(),
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return Completable.fromSingle(
            storageRepository.storeBitmap(
                params.bitmap,
                params.event,
                EMOTION_AVATAR_FILENAME
            ).doOnSuccess {
                System.out.println("Test SUCCESS")
                params.event.emotionAvatar = it
            }
        )
    }

    data class Params(val bitmap: Bitmap, val event: Event)
}