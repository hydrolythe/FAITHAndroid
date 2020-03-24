package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.repository.BackpackRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single

class SaveBackpackDrawingDetailUseCase(
    private val backpackRepository: BackpackRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveBackpackDrawingDetailUseCase.Params>(
    observeScheduler
) {
    private var params: Params? = null

    override fun buildUseCaseObservable(params: Params): Completable {
        this.params = params

        return addDetailToBackpack(params.drawingDetail)
            //    .flatMap { storageRepository.saveBackpackDetail(it) }
            .flatMapMaybe { backpackRepository.insertDetail(params.drawingDetail, params.user) }
            .flatMapCompletable { Completable.complete() }
    }

    private fun addDetailToBackpack(drawingDetail: DrawingDetail): Single<Detail> = Single.fromCallable {
        params!!.user.backpack.addDetail(drawingDetail)
        drawingDetail
    }

    data class Params(val user: User, val drawingDetail: DrawingDetail)
}