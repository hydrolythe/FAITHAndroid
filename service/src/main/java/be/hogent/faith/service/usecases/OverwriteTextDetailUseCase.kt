package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

/**
 * Use case to be used when the text inside an existing [TextDetail] should be overwritten
 * with text. This means the location of the file in the [TextDetail] is not changed.
 */
class OverwriteTextDetailUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<OverwriteTextDetailUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return storageRepository.overwriteTextDetail(params.text, params.detail)
    }

    data class Params(
        val text: String,
        val detail: TextDetail
    )
}