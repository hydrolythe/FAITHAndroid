package be.hogent.faith.service.di

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesUseCase
import be.hogent.faith.service.usecases.detail.audioDetail.CreateAudioDetailUseCase
import be.hogent.faith.service.usecases.detail.drawingDetail.CreateDrawingDetailUseCase
import be.hogent.faith.service.usecases.detail.drawingDetail.OverwriteDrawingDetailUseCase
import be.hogent.faith.service.usecases.detail.externalVideo.CreateExternalVideoDetailUseCase
import be.hogent.faith.service.usecases.detail.photoDetail.CreatePhotoDetailUseCase
import be.hogent.faith.service.usecases.detail.textDetail.CreateTextDetailUseCase
import be.hogent.faith.service.usecases.detail.textDetail.LoadTextDetailUseCase
import be.hogent.faith.service.usecases.detail.textDetail.OverwriteTextDetailUseCase
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.detailscontainer.LoadDetailFileUseCase
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
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
import be.hogent.faith.service.usecases.user.RegisterUserUseCase
import org.koin.dsl.module

/**
 * Module containing all the use cases.
 * Don't forget to add any use case that you write here so it can be injected in the app module.
 */
val serviceModule = module {
    factory { GetEventsUseCase(get(), get(), get()) }
    factory { SaveEventUseCase(get(), get(), get(), get()) }
    factory { CreateUserUseCase(get(), get()) }
    factory { SaveEmotionAvatarUseCase(get(), get()) }
    factory { SaveEventDetailUseCase(get(), get()) }
    factory { GetUserUseCase(get(), get(), get(), get(), get()) }
    factory { RegisterUserUseCase(get(), get(), get()) }
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
    factory { CreateExternalVideoDetailUseCase(get()) }
    factory { MakeEventFilesAvailableUseCase(get(), get(), get(), get()) }
    factory {
        LoadDetailFileUseCase<Backpack>(
            get(),
            get(),
            get(),
            get()
        )
    }
    factory { GetBackPackFilesUseCase(get(), get()) }
    factory { SaveDetailsContainerDetailUseCase<Backpack>(get(), get(), get(), get()) }
    factory { DeleteDetailsContainerDetailUseCase<Backpack>(get(), get()) }
}
