package be.hogent.faith.service.usecases.detail

import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single
import java.io.File

class LoadDetailFileUseCase(
    private val storageRepo: IFileStorageRepository,
    observeScheduler: Scheduler
) : SingleUseCase<File, LoadDetailFileUseCase.Params>(observeScheduler) {

    override fun buildUseCaseSingle(params: Params): Single<File> {
        return storageRepo.downloadFile(params.detail)
    }

    class Params(
        val detail: Detail
    )
}
