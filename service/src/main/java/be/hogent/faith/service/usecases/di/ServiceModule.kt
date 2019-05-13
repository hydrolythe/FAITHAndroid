package be.hogent.faith.service.usecases.di

import be.hogent.faith.service.usecases.CreateUserUseCase
import be.hogent.faith.service.usecases.GetEventsUseCase
import be.hogent.faith.service.usecases.GetUserUseCase
import be.hogent.faith.service.usecases.SaveEventAudioUseCase
import be.hogent.faith.service.usecases.SaveEmotionAvatarUseCase
import be.hogent.faith.service.usecases.SaveEventUseCase
import be.hogent.faith.service.usecases.SaveEventPhotoUseCase
import org.koin.dsl.module.module

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
    factory { SaveEventPhotoUseCase(get(), get()) }
    factory { SaveEventAudioUseCase(get(), get()) }
}
