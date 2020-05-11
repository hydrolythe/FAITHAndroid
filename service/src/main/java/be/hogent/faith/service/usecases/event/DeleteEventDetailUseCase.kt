package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

class DeleteEventDetailUseCase(
    observer: Scheduler
) : CompletableUseCase<DeleteEventDetailUseCase.Params>(observer) {

    override fun buildUseCaseObservable(params: Params): Completable {
        return Completable.fromAction {
            with(params) {
                if (detail.file.exists()) {
                    detail.file.delete()
                }
                event.removeDetail(detail)
            }
        }
    }

    data class Params(
        val detail: Detail,
        val event: Event
    )
}
