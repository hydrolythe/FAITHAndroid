package be.hogent.faith.service.di

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.TreasureChest
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
import be.hogent.faith.service.usecases.goal.AddNewGoalUseCase
import be.hogent.faith.service.usecases.goal.DeleteGoalUseCase
import be.hogent.faith.service.usecases.goal.GetGoalsUseCase
import be.hogent.faith.service.usecases.goal.SaveGoalUseCase
import be.hogent.faith.service.usecases.goal.UpdateGoalUseCase
import be.hogent.faith.service.usecases.user.GetUserUseCase
import be.hogent.faith.service.usecases.user.InitialiseUserUseCase
import be.hogent.faith.service.usecases.user.IsUsernameUniqueUseCase
import be.hogent.faith.service.usecases.user.LoginUserUseCase
import be.hogent.faith.service.usecases.user.LogoutUserUseCase
import be.hogent.faith.util.ThumbnailProvider
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Module containing all the use cases.
 * Don't forget to add any use case that you write here so it can be injected in the app module.
 */
object BackpackNames {
    private const val name = "Backpack"
    const val repo = "${name}Repository"
    const val database = "${name}Database"
    const val encryptionService = "${name}EncryptionService"
    const val loadDetailUseCase = "${name}DetailUseCase"
    const val saveDetailUseCase = "${name}SaveDetailUseCase"
    const val deleteDetailUseCase = "${name}SaveDetailUseCase"
    const val getDetailsUseCase = "${name}GetDetailsUseCase"
}

object CinemaNames {
    private const val name = "Cinema"
    const val repo = "${name}Repository"
    const val database = "${name}Database"
    const val encryptionService = "${name}EncryptionService"
    const val loadDetailUseCase = "${name}DetailUseCase"
    const val saveDetailUseCase = "${name}SaveDetailUseCase"
    const val deleteDetailUseCase = "${name}SaveDetailUseCase"
    const val getDetailsUseCase = "${name}GetDetailsUseCase"
}

object TreasureChestNames {
    private const val name = "TreasureChest"
    const val repo = "${name}Repository"
    const val database = "${name}Database"
    const val encryptionService = "${name}EncryptionService"
    const val loadDetailUseCase = "${name}DetailUseCase"
    const val saveDetailUseCase = "${name}SaveDetailUseCase"
    const val deleteDetailUseCase = "${name}SaveDetailUseCase"
    const val getDetailsUseCase = "${name}GetDetailsUseCase"
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
        InitialiseUserUseCase(
            userRepository = get(),
            backpackRepository = get(named(BackpackNames.repo)),
            cinemaRepository = get(named(CinemaNames.repo)),
            backpackEncryptionService = get(named(BackpackNames.encryptionService)),
            cinemaEncryptionService = get(named(CinemaNames.encryptionService)),
            treasureChestEncryptionService = get(named(TreasureChestNames.encryptionService)),
            treasureChestRepository = get(named(TreasureChestNames.repo)),
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
    factory { CreateDrawingDetailUseCase(get(), get(), get()) }
    factory { OverwriteDrawingDetailUseCase(get(), get(), get()) }
    factory { OverwriteTextDetailUseCase(get(), get()) }
    factory { CreatePhotoDetailUseCase(get(), get()) }
    factory { CreateAudioDetailUseCase(get()) }
    factory { CreateTextDetailUseCase(get(), get()) }
    factory { CreateVideoDetailUseCase(get()) }
    factory { DeleteEventDetailUseCase(get()) }
    factory { DeleteEventUseCase(get(), get(), get()) }
    factory {
        AddFilmToCinemaUseCase(
            containerEncryptionService = get(named(CinemaNames.encryptionService)),
            cinemaRepository = get(named(CinemaNames.repo)),
            fileStorageRepository = get(),
            observer = get()
        )
    }
    single { ThumbnailProvider() }
    factory {
        MakeEventFilesAvailableUseCase(
            fileStorageRepo = get(),
            eventRepository = get(),
            eventEncryptionService = get(),
            observer = get()
        )
    }
    factory { AddNewGoalUseCase(get(), get(), get()) }
    factory<LoadDetailFileUseCase<Backpack>>(named(BackpackNames.loadDetailUseCase)) {
        LoadDetailFileUseCase<Backpack>(
            storageRepo = get(),
            containerRepository = get(named(BackpackNames.repo)),
            detailContainerEncryptionService = get(named(BackpackNames.encryptionService)),
            observer = get()
        )
    }
    factory<LoadDetailFileUseCase<TreasureChest>>(named(TreasureChestNames.loadDetailUseCase)) {
        LoadDetailFileUseCase<TreasureChest>(
            storageRepo = get(),
            containerRepository = get(named(TreasureChestNames.repo)),
            detailContainerEncryptionService = get(named(TreasureChestNames.encryptionService)),
            observer = get()
        )
    }
    factory<LoadDetailFileUseCase<Cinema>>(named(CinemaNames.loadDetailUseCase)) {
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
    factory<SaveDetailsContainerDetailUseCase<Backpack>>(named(BackpackNames.saveDetailUseCase)) {
        SaveDetailsContainerDetailUseCase<Backpack>(
            detailContainerRepository = get(named(BackpackNames.repo)),
            detailContainerEncryptionService = get(named(BackpackNames.encryptionService)),
            storageRepository = get(),
            observer = get()
        )
    }
    factory<SaveDetailsContainerDetailUseCase<TreasureChest>>(named(TreasureChestNames.saveDetailUseCase)) {
        SaveDetailsContainerDetailUseCase<TreasureChest>(
            detailContainerRepository = get(named(TreasureChestNames.repo)),
            detailContainerEncryptionService = get(named(TreasureChestNames.encryptionService)),
            storageRepository = get(),
            observer = get()
        )
    }
    factory<SaveDetailsContainerDetailUseCase<Cinema>>(named(CinemaNames.saveDetailUseCase)) {
        SaveDetailsContainerDetailUseCase<Cinema>(
            detailContainerRepository = get(named(CinemaNames.repo)),
            detailContainerEncryptionService = get(named(CinemaNames.encryptionService)),
            storageRepository = get(),
            observer = get()
        )
    }
    factory<DeleteDetailsContainerDetailUseCase<Backpack>>(qualifier = named(BackpackNames.deleteDetailUseCase)) {
        DeleteDetailsContainerDetailUseCase<Backpack>(
            backpackRepository = get(named(BackpackNames.repo)),
            fileStorageRepository = get(),
            observer = get()
        )
    }
    factory<DeleteDetailsContainerDetailUseCase<TreasureChest>>(named(TreasureChestNames.deleteDetailUseCase)) {
        DeleteDetailsContainerDetailUseCase<TreasureChest>(
            backpackRepository = get(named(TreasureChestNames.repo)),
            fileStorageRepository = get(),
            observer = get()
        )
    }
    factory<DeleteDetailsContainerDetailUseCase<Cinema>>(named(CinemaNames.deleteDetailUseCase)) {
        DeleteDetailsContainerDetailUseCase<Cinema>(
            backpackRepository = get(named(CinemaNames.repo)),
            fileStorageRepository = get(),
            observer = get()
        )
    }
    factory<GetDetailsContainerDataUseCase<Backpack>>(named(BackpackNames.getDetailsUseCase)) {
        GetDetailsContainerDataUseCase<Backpack>(
            detailsContainerRepository = get(named(BackpackNames.repo)),
            detailContainerEncryptionService = get(named(BackpackNames.encryptionService)),
            observer = get()
        )
    }
    factory<GetDetailsContainerDataUseCase<TreasureChest>>(named(TreasureChestNames.getDetailsUseCase)) {
        GetDetailsContainerDataUseCase<TreasureChest>(
            detailsContainerRepository = get(named(TreasureChestNames.repo)),
            detailContainerEncryptionService = get(named(TreasureChestNames.encryptionService)),
            observer = get()
        )
    }
    factory<GetDetailsContainerDataUseCase<Cinema>>(named(CinemaNames.getDetailsUseCase)) {
        GetDetailsContainerDataUseCase<Cinema>(
            detailsContainerRepository = get(named(CinemaNames.repo)),
            detailContainerEncryptionService = get(named(CinemaNames.encryptionService)),
            observer = get()
        )
    }
    factory {
        CreateCinemaVideoUseCase(
            videoEncoder = VideoEncoder(),
            loadFileUseCase = get(named("LoadCinemaDetailFileUseCase")),
            thumbnailProvider = get(),
            observer = get()
        )
    }
    factory { DeleteGoalUseCase(get(), get()) }
    factory { SaveGoalUseCase(get(), get(), get()) }
    factory { UpdateGoalUseCase(get(), get(), get()) }
    factory { GetGoalsUseCase(get(), get(), get()) }
}
