package be.hogent.faith.database.di

import be.hogent.faith.database.daos.DetailDao
import be.hogent.faith.database.daos.EventDao
import be.hogent.faith.database.database.EntityDatabase
import be.hogent.faith.database.mappers.EventMapper
import be.hogent.faith.database.repositories.EventRepositoryImpl
import be.hogent.faith.domain.repository.EventRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val databaseModule = module {
    single { EventMapper() }
    single { EntityDatabase.getDatabase(androidContext()) }
    single { constructEventDao(get()) }
    single { constructDetailDao(get()) }
    // Just specifying the aventRepositoryImpl is not enough.
    // In other modules some elements require an EventRepository as constructor parameter.
    // Koin doesn't automatically see the Impl as an implementation of the interface,
    // so we have to explicitly mention it.
    single { EventRepositoryImpl(get(), get()) as EventRepository }
}

fun constructEventDao(entityDatabase: EntityDatabase): EventDao {
    return entityDatabase.eventDao()
}

fun constructDetailDao(entityDatabase: EntityDatabase): DetailDao {
    return entityDatabase.detailDao()
}
