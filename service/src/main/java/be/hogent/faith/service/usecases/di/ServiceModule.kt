package be.hogent.faith.service.usecases.di

import be.hogent.faith.service.usecases.CreateUserUseCase
import be.hogent.faith.service.usecases.GetUserUseCase
import be.hogent.faith.service.usecases.IsUsernameUniqueUseCase
import be.hogent.faith.service.usecases.LoginUserUseCase
import be.hogent.faith.service.usecases.LogoutUserUseCase
import be.hogent.faith.service.usecases.RegisterUserUseCase
import be.hogent.faith.service.usecases.detail.LoadDetailFileUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackTextDetailUseCase
import be.hogent.faith.service.usecases.backpack.DeleteBackpackDetailUseCase
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesDummyUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackAudioDetailUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackDrawingDetailUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackExternalVideoDetailUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackPhotoDetailUseCase
import be.hogent.faith.service.usecases.detail.audioDetail.CreateAudioDetailUseCase
import be.hogent.faith.service.usecases.detail.drawingDetail.CreateDrawingDetailUseCase
import be.hogent.faith.service.usecases.detail.drawingDetail.OverwriteDrawingDetailUseCase
import be.hogent.faith.service.usecases.detail.externalVideo.CreateExternalVideoDetailUseCase
import be.hogent.faith.service.usecases.detail.photoDetail.CreatePhotoDetailUseCase
import be.hogent.faith.service.usecases.detail.textDetail.CreateTextDetailUseCase
import be.hogent.faith.service.usecases.detail.textDetail.LoadTextDetailUseCase
import be.hogent.faith.service.usecases.detail.textDetail.OverwriteTextDetailUseCase
import be.hogent.faith.service.usecases.event.SaveEventDetailUseCase
import be.hogent.faith.service.usecases.event.GetEventFilesUseCase
import be.hogent.faith.service.usecases.event.GetEventsUseCase
import be.hogent.faith.service.usecases.event.SaveEmotionAvatarUseCase
import be.hogent.faith.service.usecases.event.SaveEventUseCase
import org.koin.dsl.module

/**
 * Module containing all the use cases.
 * Don't forget to add any use  case that you write here so it can be injected in the app module.
 */
val serviceModule = module {
    factory { GetEventsUseCase(get(), get()) }
    factory { SaveEventUseCase(get(), get(), get()) }
    factory { CreateUserUseCase(get(), get()) }
    factory { SaveEmotionAvatarUseCase(get(), get()) }
    factory {
        SaveEventDetailUseCase(
            get(),
            get()
        )
    }
    factory { GetUserUseCase(get(), get(), get(), get()) }
    factory { RegisterUserUseCase(get(), get(), get()) }
    factory { IsUsernameUniqueUseCase(get(), get()) }
    factory { LoginUserUseCase(get(), get()) }
    factory { LogoutUserUseCase(get(), get()) }
    factory { LoadTextDetailUseCase(get(), get(), get()) }
    factory { CreateDrawingDetailUseCase(get(), get()) }
    factory { OverwriteDrawingDetailUseCase(get(), get()) }
    factory { OverwriteTextDetailUseCase(get(), get()) }
    factory { CreatePhotoDetailUseCase(get()) }
    factory { CreateAudioDetailUseCase(get()) }
    factory { CreateTextDetailUseCase(get(), get()) }
    factory { CreateExternalVideoDetailUseCase(get()) }
    factory { GetEventFilesUseCase(get(), get()) }
    factory { LoadDetailFileUseCase(get(), get()) }
    factory { GetBackPackFilesDummyUseCase(get(), get()) }
    factory { SaveBackpackTextDetailUseCase(get(), get()) }
    factory { SaveBackpackAudioDetailUseCase(get(), get()) }
    factory { SaveBackpackPhotoDetailUseCase(get(), get()) }
    factory { SaveBackpackDrawingDetailUseCase(get(), get()) }
    factory { SaveBackpackExternalVideoDetailUseCase(get(), get()) }
    factory { DeleteBackpackDetailUseCase(get(), get()) }
}
