package be.hogent.faith.service.di

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.service.usecases.backpack.GetYoutubeVideosFromSearchUseCase
import be.hogent.faith.service.usecases.cinema.AddFilmToCinemaUseCase
import be.hogent.faith.service.usecases.cinema.CreateCinemaVideoUseCase
import be.hogent.faith.service.usecases.cinema.VideoEncoder
import be.hogent.faith.service.usecases.detail.audioDetail.CreateAudioDetailUseCase
import be.hogent.faith.service.usecases.detail.drawingDetail.CreateDrawingDetailUseCase
import be.hogent.faith.service.usecases.detail.drawingDetail.OverwriteDrawingDetailUseCase
import be.hogent.faith.service.usecases.detail.externalVideo.CreateVideoDetailUseCase
import be.hogent.faith.service.usecases.detail.photoDetail.CreatePhotoDetailUseCase
import be.hogent.faith.service.usecases.detail.textDetail.CreateTextDetailUseCase
import be.hogent.faith.service.usecases.detail.textDetail.LoadTextDetailUseCase
import be.hogent.faith.service.usecases.detail.textDetail.OverwriteTextDetailUseCase
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.detailscontainer.GetDetailsContainerDataUseCase
import be.hogent.faith.service.usecases.detailscontainer.LoadDetailFileUseCase
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.event.DeleteEventDetailUseCase
import be.hogent.faith.service.usecases.event.DeleteEventUseCase
import be.hogent.faith.service.usecases.event.GetEventsUseCase
import be.hogent.faith.service.usecases.event.MakeEventFilesAvailableUseCase
import be.hogent.faith.service.usecases.event.SaveEmotionAvatarUseCase
import be.hogent.faith.service.usecases.event.SaveEventDetailUseCase
import be.hogent.faith.service.usecases.event.SaveEventUseCase
import be.hogent.faith.service.usecases.user.CreateUserUseCase
import be.hogent.faith.service.usecases.user.GetUserUseCase
import be.hogent.faith.service.usecases.user.IsUsernameUniqueUseCase
import be.hogent.faith.service.usecases.user.LoginUserUseCase
import be.hogent.faith.service.usecases.user.LogoutUserUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Module containing all the use cases.
 * Don't forget to add any use case that you write here so it can be injected in the app module.
 */
object BackpackNames {
    const val repo = "BackpackRepository"
    const val database = "BackpackDatabase"
    const val encryptionService = "BackpackEncryptionService"
}

object CinemaNames {
    const val repo = "CinemaRepository"
    const val database = "CinemaDatabase"
    const val encryptionService = "CinemaEncryptionService"
}

val serviceModule = module {
    factory {
        GetEventsUseCase(
            eventRepository = get(),
            eventEncryptionService = get(),
            observer = get()
        )
    }
    factory {
        SaveEventUseCase(
            eventEncryptionService = get(),
            filesStorageRepository = get(),
            eventRepository = get(),
            observer = get()
        )
    }
    factory {
        CreateUserUseCase(
            authManager = get(),
            userRepository = get(),
            backpackRepository = get(named(BackpackNames.repo)),
            cinemaRepository = get(named(CinemaNames.repo)),
            backpackEncryptionService = get(),
            cinemaEncryptionService = get(),
            observer = get()
        )
    }
    factory { SaveEmotionAvatarUseCase(get(), get()) }
    factory { SaveEventDetailUseCase(get(), get()) }
    factory {
        GetUserUseCase(
            userRepository = get(),
            eventRepository = get(),
            eventEncryptionService = get(),
            authManager = get(),
            observeScheduler = get()
        )
    }
    factory { IsUsernameUniqueUseCase(get(), get()) }
    factory { LoginUserUseCase(get(), get()) }
    factory { LogoutUserUseCase(get(), get()) }
    factory { LoadTextDetailUseCase(get(), get()) }
    factory { CreateDrawingDetailUseCase(get(), get()) }
    factory { OverwriteDrawingDetailUseCase(get(), get()) }
    factory { OverwriteTextDetailUseCase(get(), get()) }
    factory { CreatePhotoDetailUseCase(get()) }
    factory { CreateAudioDetailUseCase(get()) }
    factory { CreateTextDetailUseCase(get(), get()) }
    factory { CreateVideoDetailUseCase(get()) }
    factory { DeleteEventDetailUseCase(get()) }
    factory { DeleteEventUseCase(get(), get(), get()) }
    factory { CreateCinemaVideoUseCase(VideoEncoder(), get()) }
    factory { AddFilmToCinemaUseCase(
        containerEncryptionService = get(named(CinemaNames.encryptionService)),
        cinemaRepository = get(named(CinemaNames.repo)),
        fileStorageRepository = get(),
        observer = get()
    )}
    factory {
        MakeEventFilesAvailableUseCase(
            fileStorageRepo = get(),
            eventRepository = get(),
            eventEncryptionService = get(),
            observer = get()
        )
    }
    factory<LoadDetailFileUseCase<Backpack>>(named("LoadBackpackDetailFileUseCase")) {
        LoadDetailFileUseCase<Backpack>(
            storageRepo = get(),
            containerRepository = get(named(BackpackNames.repo)),
            detailContainerEncryptionService = get(named(BackpackNames.encryptionService)),
            observer = get()
        )
    }
    factory<LoadDetailFileUseCase<Cinema>>(named("LoadCinemaDetailFileUseCase")) {
        LoadDetailFileUseCase<Cinema>(
            storageRepo = get(),
            containerRepository = get(named(CinemaNames.repo)),
            detailContainerEncryptionService = get(named(CinemaNames.encryptionService)),
            observer = get()
        )
    }
    factory {
        GetYoutubeVideosFromSearchUseCase(get())
    }
    factory<SaveDetailsContainerDetailUseCase<Backpack>>(named("SaveBackpackDetailUseCase")) {
        SaveDetailsContainerDetailUseCase<Backpack>(
            detailContainerRepository = get(named(BackpackNames.repo)),
            detailContainerEncryptionService = get(named(BackpackNames.encryptionService)),
            storageRepository = get(),
            observer = get()
        )
    }
    factory<SaveDetailsContainerDetailUseCase<Cinema>>(named("SaveCinemaDetailUseCase")) {
        SaveDetailsContainerDetailUseCase<Cinema>(
            detailContainerRepository = get(named(CinemaNames.repo)),
            detailContainerEncryptionService = get(named(CinemaNames.encryptionService)),
            storageRepository = get(),
            observer = get()
        )
    }
    factory<DeleteDetailsContainerDetailUseCase<Backpack>>(qualifier = named("DeleteBackpackDetailUseCase")) {
        DeleteDetailsContainerDetailUseCase<Backpack>(
            backpackRepository = get(named(BackpackNames.repo)),
            fileStorageRepository = get(),
            observer = get()
        )
    }
    factory<DeleteDetailsContainerDetailUseCase<Cinema>>(named("DeleteCinemaDetailUseCase")) {
        DeleteDetailsContainerDetailUseCase<Cinema>(
            backpackRepository = get(named(CinemaNames.repo)),
            fileStorageRepository = get(),
            observer = get()
        )
    }
    factory<GetDetailsContainerDataUseCase<Backpack>>(named("GetBackpackDataUseCase")) {
        GetDetailsContainerDataUseCase<Backpack>(
            detailsContainerRepository = get(named(BackpackNames.repo)),
            detailContainerEncryptionService = get(named(BackpackNames.encryptionService)),
            observer = get()
        )
    }
    factory<GetDetailsContainerDataUseCase<Cinema>>(named("GetCinemaDataUseCase")) {
        GetDetailsContainerDataUseCase<Cinema>(
            detailsContainerRepository = get(named(CinemaNames.repo)),
            detailContainerEncryptionService = get(named(CinemaNames.encryptionService)),
            observer = get()
        )
    }
}
