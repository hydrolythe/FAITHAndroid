package be.hogent.faith.service.usecases.di

import be.hogent.faith.service.usecases.CreateUserUseCase
import be.hogent.faith.service.usecases.GetEventsUseCase
import be.hogent.faith.service.usecases.GetUserUseCase
import be.hogent.faith.service.usecases.SaveEmotionAvatarUseCase
import be.hogent.faith.service.usecases.SaveEventAudioUseCase
import be.hogent.faith.service.usecases.SaveEventPhotoUseCase
import be.hogent.faith.service.usecases.SaveEventUseCase
import be.hogent.faith.service.usecases.drawingDetail.CreateDrawingDetailUseCase
import be.hogent.faith.service.usecases.drawingDetail.OverwriteDrawingDetailUseCase
import be.hogent.faith.service.usecases.drawingDetail.SaveEventDrawingUseCase
import be.hogent.faith.service.usecases.photoDetail.CreatePhotoDetailUseCase
import be.hogent.faith.service.usecases.textDetail.CreateTextDetailUseCase
import be.hogent.faith.service.usecases.textDetail.LoadTextDetailUseCase
import be.hogent.faith.service.usecases.textDetail.OverwriteTextDetailUseCase
import be.hogent.faith.service.usecases.textDetail.SaveEventTextDetailUseCase
import org.koin.dsl.module

/**
 * Module containing all the use cases.
 * Don't forget to add any use  case that you write here so it can be injected in the app module.
 */
val serviceModule = module {
    // Use cases
    factory { GetEventsUseCase(get(), get()) }
    factory { SaveEventUseCase(get(), get()) }
    factory { CreateUserUseCase(get(), get()) }
    factory { SaveEmotionAvatarUseCase(get(), get()) }
    factory { GetUserUseCase(get(), get()) }
    factory { CreateTextDetailUseCase(get(), get()) }
    factory { SaveEventPhotoUseCase(get(), get()) }
    factory { SaveEventAudioUseCase(get(), get()) }
    factory { SaveEventDrawingUseCase(get(), get()) }
    factory { LoadTextDetailUseCase(get(), get()) }
    factory { CreateDrawingDetailUseCase(get(), get()) }
    factory { OverwriteDrawingDetailUseCase(get(), get()) }
    factory { SaveEventTextDetailUseCase(get(), get()) }
    factory { OverwriteTextDetailUseCase(get(), get()) }
    factory { CreatePhotoDetailUseCase(get(), get()) }
}
