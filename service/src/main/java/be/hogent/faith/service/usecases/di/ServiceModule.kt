package be.hogent.faith.service.usecases.di

import be.hogent.faith.service.usecases.CreateUserUseCase
import be.hogent.faith.service.usecases.GetEventsUseCase
import be.hogent.faith.service.usecases.GetUserUseCase
import be.hogent.faith.service.usecases.SaveEmotionAvatarUseCase
import be.hogent.faith.service.usecases.SaveEventUseCase
import be.hogent.faith.service.usecases.audioDetail.CreateAudioDetailUseCase
import be.hogent.faith.service.usecases.audioDetail.SaveEventAudioDetailUseCase
import be.hogent.faith.service.usecases.drawingDetail.CreateDrawingDetailUseCase
import be.hogent.faith.service.usecases.drawingDetail.OverwriteDrawingDetailUseCase
import be.hogent.faith.service.usecases.drawingDetail.SaveEventDrawingDetailUseCase
import be.hogent.faith.service.usecases.photoDetail.CreatePhotoDetailUseCase
import be.hogent.faith.service.usecases.photoDetail.SaveEventPhotoDetailUseCase
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
    factory { GetEventsUseCase(get(), get()) }
    factory { SaveEventUseCase(get(), get()) }
    factory { CreateUserUseCase(get(), get()) }
    factory { SaveEmotionAvatarUseCase(get(), get()) }
    factory { GetUserUseCase(get(), get()) }
    factory { CreateTextDetailUseCase(get(), get()) }
    factory { SaveEventPhotoDetailUseCase(get(), get()) }
    factory { SaveEventAudioDetailUseCase(get(), get()) }
    factory { SaveEventDrawingDetailUseCase(get(), get()) }
    factory { LoadTextDetailUseCase(get(), get()) }
    factory { CreateDrawingDetailUseCase(get(), get()) }
    factory { OverwriteDrawingDetailUseCase(get(), get()) }
    factory { SaveEventTextDetailUseCase(get(), get()) }
    factory { OverwriteTextDetailUseCase(get(), get()) }
    factory { CreatePhotoDetailUseCase(get()) }
    factory { CreateAudioDetailUseCase(get()) }
}
