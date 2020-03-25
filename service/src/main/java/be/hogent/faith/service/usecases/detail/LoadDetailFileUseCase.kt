package be.hogent.faith.service.usecases.detail

import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.usecases.base.SingleUseCase
import be.hogent.faith.storage.IStorageRepository
import io.reactivex.Scheduler
import io.reactivex.Single
import java.io.File

class LoadDetailFileUseCase(
    private val storageRepo: IStorageRepository,
    observeScheduler: Scheduler
) : SingleUseCase<File, LoadDetailFileUseCase.LoadFileParams>(observeScheduler) {

    override fun buildUseCaseSingle(params: LoadFileParams): Single<File> {
        return storageRepo.getFile(params.detail)
    }

    class LoadFileParams(
        val detail: Detail
    )
}
