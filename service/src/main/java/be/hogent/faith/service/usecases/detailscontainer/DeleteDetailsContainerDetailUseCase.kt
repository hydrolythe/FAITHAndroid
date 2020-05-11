package be.hogent.faith.service.usecases.detailscontainer

import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

class DeleteDetailsContainerDetailUseCase<T : DetailsContainer>(
    private val backpackRepository: IDetailContainerRepository<T>,
    private val fileStorageRepository: IFileStorageRepository,
    observer: Scheduler
) : CompletableUseCase<DeleteDetailsContainerDetailUseCase.Params>(observer) {

    override fun buildUseCaseObservable(params: Params): Completable {
        return Completable.mergeArray(
            backpackRepository.deleteDetail(params.detail),
            fileStorageRepository.deleteFiles(params.detail, params.container)
        ).concatWith(
            Completable.defer { Completable.fromAction { params.container.removeDetail(params.detail) } }
        )
    }

    data class Params(val detail: Detail, val container: DetailsContainer)
}
