package be.hogent.faith.service.usecases.detail.textDetail

import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.localStorage.ITemporaryStorage
import io.reactivex.Completable
import io.reactivex.Scheduler

/**
 * Use case to be used when the text inside an existing [TextDetail] should be overwritten
 * with text. This means the location of the file in the [TextDetail] is not changed.
 */
class OverwriteTextDetailUseCase(
    private val tempStorageRepo: ITemporaryStorage,
    observeScheduler: Scheduler
) : CompletableUseCase<OverwriteTextDetailUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return tempStorageRepo.overwriteTextDetail(params.text, params.detail)
    }

    data class Params(
        val text: String,
        val detail: TextDetail
    )
}