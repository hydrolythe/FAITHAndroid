package be.hogent.faith.database.di

import be.hogent.faith.database.firebase.FirebaseAuthManager
import be.hogent.faith.database.firebase.FirebaseEventRepository
import be.hogent.faith.database.firebase.FirebaseUserRepository
import be.hogent.faith.database.mappers.EventMapper
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.database.repositories.AuthManagerImpl
import be.hogent.faith.database.repositories.EventRepositoryImpl
import be.hogent.faith.database.repositories.UserRepositoryImpl
import be.hogent.faith.domain.repository.AuthManager
import be.hogent.faith.domain.repository.EventRepository
import be.hogent.faith.domain.repository.UserRepository
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
    single { EventRepositoryImpl(get(), get(), get()) as EventRepository }
    single { UserRepositoryImpl(get(), get()) as UserRepository }
    single { AuthManagerImpl(get()) as AuthManager }
    single { FirebaseAuthManager(constructFirebaseAuthInstance()) }
    single { FirebaseUserRepository(constructFirebaseAuthInstance(), constructFireStoreInstance()) }
    single {
        FirebaseEventRepository(
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
