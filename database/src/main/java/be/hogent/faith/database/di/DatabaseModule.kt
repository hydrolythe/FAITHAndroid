package be.hogent.faith.database.di

import be.hogent.faith.database.event.EventDatabase
import be.hogent.faith.database.authentication.FirebaseAuthManager
import be.hogent.faith.database.detailcontainer.BackpackDatabase
import be.hogent.faith.database.detailcontainer.DetailContainerDatabase
import be.hogent.faith.database.event.EventMapper
import be.hogent.faith.database.user.UserMapper
import be.hogent.faith.database.authentication.AuthManager
import be.hogent.faith.database.authentication.BackpackRepository
import be.hogent.faith.database.event.EventRepository
import be.hogent.faith.database.user.UserRepository
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.service.usecases.repository.IDetailContainerRepository
import be.hogent.faith.service.usecases.repository.IAuthManager
import be.hogent.faith.service.usecases.repository.IBackpackRepository
import be.hogent.faith.service.usecases.repository.IEventRepository
import be.hogent.faith.service.usecases.repository.IUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.dsl.module

val databaseModule = module {
    single { EventMapper }
    single { UserMapper }

    // Just specifying the eventRepositoryImpl is not enough.
    // In other modules some elements require an EventRepository as constructor parameter.
    // Koin doesn't automatically see the Impl as an implementation of the interface,
    // so we have to explicitly mention it.
    single { EventRepository(
        get(),
        get(),
        get()
    ) as IEventRepository }
    single { BackpackRepository(get(), get(), get()) as IBackpackRepository }
    single {
        BackpackRepository.DetailContainerRepositoryImpl<Backpack>(
            get(),
            get(),
            get()
        ) as IDetailContainerRepository<Backpack>
    }
    single { UserRepository(get(), get()) as IUserRepository }
    single { AuthManager(get()) as IAuthManager }
    single {
        FirebaseAuthManager(
            constructFirebaseAuthInstance()
        )
    }
    single {
        BackpackDatabase(
            constructFirebaseAuthInstance(),
            constructFireStoreInstance()
        ) as DetailContainerDatabase<Backpack>
    }
    single {
        EventDatabase(
            constructFirebaseAuthInstance(),
            constructFireStoreInstance()
        )
    }
}

fun constructFireStoreInstance(): FirebaseFirestore {
    return FirebaseFirestore.getInstance()
}

fun constructFirebaseAuthInstance(): FirebaseAuth {
    return FirebaseAuth.getInstance()
}
