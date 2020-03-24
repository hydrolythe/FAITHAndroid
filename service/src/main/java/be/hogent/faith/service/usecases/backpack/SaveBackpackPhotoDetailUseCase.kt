package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.repository.BackpackRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single

class SaveBackpackPhotoDetailUseCase(
    private val backpackRepository: BackpackRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveBackpackPhotoDetailUseCase.Params>(
    observeScheduler
) {

    private var params: Params? = null

    override fun buildUseCaseObservable(params: Params): Completable {
        this.params = params

        return addDetailToBackpack(params.photoDetail)

            .flatMapMaybe { backpackRepository.insertDetail(params.photoDetail, params.user) }
            .flatMapCompletable { Completable.complete() }
    }

    private fun addDetailToBackpack(photoDetail: PhotoDetail): Single<Detail> = Single.fromCallable {
        params!!.user.backpack.addDetail(photoDetail)
        photoDetail
    }

    data class Params(val user: User, val photoDetail: PhotoDetail)
}