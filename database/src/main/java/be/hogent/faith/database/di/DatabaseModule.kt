package be.hogent.faith.database.di

import be.hogent.faith.database.authentication.AuthManager
import be.hogent.faith.database.authentication.FirebaseAuthManager
import be.hogent.faith.database.detailcontainer.BackpackDatabase
import be.hogent.faith.database.detailcontainer.DetailContainerDatabase
import be.hogent.faith.database.detailcontainer.DetailContainerRepository
import be.hogent.faith.database.event.EventDatabase
import be.hogent.faith.database.event.EventMapper
import be.hogent.faith.database.event.EventRepository
import be.hogent.faith.database.user.UserMapper
import be.hogent.faith.database.user.UserRepository
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.service.repositories.IAuthManager
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.repositories.IEventRepository
import be.hogent.faith.service.repositories.IUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.dsl.module

val databaseModule = module {
    single { EventMapper }
    single { UserMapper }

    single { EventRepository(get()) as IEventRepository }
    single {
        DetailContainerRepository<Backpack>(get(), get(), get()) as IDetailContainerRepository
    }
    single { UserRepository(get(), get()) as IUserRepository }
    single { AuthManager(get()) as IAuthManager }
    single { FirebaseAuthManager(constructFirebaseAuthInstance()) }
    single {
        BackpackDatabase(
            constructFirebaseAuthInstance(),
            constructFireStoreInstance()
        ) as DetailContainerDatabase<Backpack>
    }
    single { EventDatabase(constructFirebaseAuthInstance(), constructFireStoreInstance()) }
}

fun constructFireStoreInstance(): FirebaseFirestore {
    return FirebaseFirestore.getInstance()
}

fun constructFirebaseAuthInstance(): FirebaseAuth {
    return FirebaseAuth.getInstance()
}
