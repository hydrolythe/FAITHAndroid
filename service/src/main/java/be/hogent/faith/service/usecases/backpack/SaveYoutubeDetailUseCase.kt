package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.repository.DetailContainerRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single

/**
 * YouTube detail isn't stored in a file
 */
class SaveYoutubeDetailUseCase(
    private val backpackRepository: DetailContainerRepository<Backpack>,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveYoutubeDetailUseCase.Params>(
    observeScheduler
) {
    private var params: Params? = null

    override fun buildUseCaseObservable(params: Params): Completable {
        this.params = params

        return addDetailToBackpack(params.youtubeDetail)
            .flatMapMaybe { backpackRepository.insertDetail(params.youtubeDetail, params.user) }
            .flatMapCompletable { Completable.complete() }
    }

    private fun addDetailToBackpack(youtubeDetail: YoutubeVideoDetail): Single<Detail> = Single.fromCallable {
        params!!.user.backpack.addDetail(youtubeDetail)
        youtubeDetail
    }

    data class Params(val user: User, val youtubeDetail: YoutubeVideoDetail)
}