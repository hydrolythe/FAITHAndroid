package be.hogent.faith.service.usecases.di

import be.hogent.faith.service.usecases.CreateUserUseCase
import be.hogent.faith.service.usecases.GetEventsUseCase
import be.hogent.faith.service.usecases.GetUserUseCase
import be.hogent.faith.service.usecases.LoginUserUseCase
import be.hogent.faith.service.usecases.LogoutUserUseCase
import be.hogent.faith.service.usecases.RegisterUserUseCase
import be.hogent.faith.service.usecases.LoadTextDetailUseCase
import be.hogent.faith.service.usecases.SaveEmotionAvatarUseCase
import be.hogent.faith.service.usecases.SaveEventAudioUseCase
import be.hogent.faith.service.usecases.SaveEventDrawingUseCase
import be.hogent.faith.service.usecases.SaveEventPhotoUseCase
import be.hogent.faith.service.usecases.SaveEventTextUseCase
import be.hogent.faith.service.usecases.SaveEventUseCase
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
    factory { GetUserUseCase(get(), get(), get()) }
    factory { RegisterUserUseCase(get(), get()) }
    factory { LoginUserUseCase(get(), get()) }
    factory { LogoutUserUseCase(get(), get()) }
    factory { SaveEventTextUseCase(get(), get()) }
    factory { SaveEventPhotoUseCase(get(), get()) }
    factory { SaveEventAudioUseCase(get(), get()) }
    factory { SaveEventDrawingUseCase(get(), get()) }
    factory { LoadTextDetailUseCase(get(), get()) }
}
