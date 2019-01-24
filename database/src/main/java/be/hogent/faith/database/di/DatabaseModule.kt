package be.hogent.faith.database.di

import be.hogent.faith.database.database.EntityDatabase
import be.hogent.faith.database.mappers.EventMapper
import be.hogent.faith.database.repositories.EventRepositoryImpl
import be.hogent.faith.domain.repository.EventRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val databaseModule = module {
    single { EventMapper() }
    single { EntityDatabase.getDatabase(androidContext()) }
    single { EventRepositoryImpl(get(), get()) as EventRepository}
}
