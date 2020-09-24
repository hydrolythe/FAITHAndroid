package be.hogent.faith.service.usecases.detail.textDetail

import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.repositories.ITemporaryFileStorageRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler

/**
 * Use case to be used when the text inside an existing [TextDetail] should be overwritten
 * with text. This means the location of the file in the [TextDetail] is not changed.
 */
class OverwriteTextDetailUseCase(
    private val tempStorageRepo: ITemporaryFileStorageRepository,
    observer: Scheduler
) : CompletableUseCase<OverwriteTextDetailUseCase.Params>(observer) {

    override fun buildUseCaseObservable(params: Params): Completable {
        return tempStorageRepo.overwriteTextDetail(params.text, params.detail)
    }

    data class Params(
        val text: String,
        val detail: TextDetail
    )
}