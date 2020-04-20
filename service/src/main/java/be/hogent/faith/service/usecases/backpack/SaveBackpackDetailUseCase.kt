package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.repository.BackpackRepository
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
import be.hogent.faith.storage.IStorageRepository
import io.reactivex.Scheduler

class SaveBackpackDetailUseCase(
    backpackRepository: BackpackRepository,
    storageRepository: IStorageRepository,
    observeScheduler: Scheduler
) : SaveDetailsContainerDetailUseCase<Backpack>(
    backpackRepository,
    storageRepository,
    observeScheduler
)
