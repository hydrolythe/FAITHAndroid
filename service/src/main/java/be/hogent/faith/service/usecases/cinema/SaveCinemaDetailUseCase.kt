package be.hogent.faith.service.usecases.cinema

import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.repository.CinemaRepository
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
import be.hogent.faith.storage.IStorageRepository
import io.reactivex.Scheduler

class SaveCinemaDetailUseCase(
    cinemaRepository: CinemaRepository,
    storageRepository: IStorageRepository,
    observeScheduler: Scheduler
) : SaveDetailsContainerDetailUseCase<Cinema>(cinemaRepository, storageRepository, observeScheduler)
