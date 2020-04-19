package be.hogent.faith.service.usecases.cinema

import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.repository.CinemaRepository
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import io.reactivex.Scheduler

class DeleteCinemaDetailUseCase(
    detailsContainerRepository: CinemaRepository,
    observeScheduler: Scheduler
) : DeleteDetailsContainerDetailUseCase<Cinema>(detailsContainerRepository, observeScheduler)