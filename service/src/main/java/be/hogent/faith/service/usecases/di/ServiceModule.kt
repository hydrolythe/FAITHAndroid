package be.hogent.faith.service.usecases.di

import be.hogent.faith.service.usecases.CreateEventUseCase
import be.hogent.faith.service.usecases.GetEventsUseCase
import org.koin.dsl.module.module

val serviceModule = module {
    // Use cases
    factory { GetEventsUseCase(get(), get()) }
    factory { CreateEventUseCase(get(), get()) }
}
