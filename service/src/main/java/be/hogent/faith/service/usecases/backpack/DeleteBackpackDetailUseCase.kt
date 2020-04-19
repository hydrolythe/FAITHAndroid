package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.repository.BackpackRepository
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import io.reactivex.Scheduler

class DeleteBackpackDetailUseCase(
    detailsContainerRepository: BackpackRepository,
    observeScheduler: Scheduler
) : DeleteDetailsContainerDetailUseCase<Backpack>(detailsContainerRepository, observeScheduler)