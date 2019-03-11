package be.hogent.faith.service.usecases.di

import be.hogent.faith.service.usecases.CreateEventUseCase
import be.hogent.faith.service.usecases.GetEventsUseCase
import org.koin.dsl.module.module

/**
 * Module containing all the use cases.
 * Don't forget to add any use  case that you write here so it can be injected in the app module.
 */
val serviceModule = module {
    // Use cases
    factory { GetEventsUseCase(get(), get()) }
    factory { CreateEventUseCase(get(), get()) }
}
