package be.hogent.faith.service.usecases

import be.hogent.faith.service.usecases.base.SingleUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Scheduler
import io.reactivex.Single
import java.io.File

class LoadTextDetailUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : SingleUseCase<String, LoadTextDetailUseCase.LoadTextParams>(observeScheduler) {

    override fun buildUseCaseSingle(params: LoadTextParams): Single<String> {
        return storageRepository.loadText(params.saveFile)
    }

    class LoadTextParams(
        val saveFile: File
    )
}
