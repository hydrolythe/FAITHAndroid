package be.hogent.faith.database.di

import be.hogent.faith.database.authentication.AuthManager
import be.hogent.faith.database.authentication.FirebaseAuthManager
import be.hogent.faith.database.common.DetailMapper
import be.hogent.faith.database.detailcontainer.BackpackDatabase
import be.hogent.faith.database.detailcontainer.CinemaDatabase
import be.hogent.faith.database.detailcontainer.DetailContainerDatabase
import be.hogent.faith.database.detailcontainer.DetailContainerMapper
import be.hogent.faith.database.detailcontainer.DetailContainerRepository
import be.hogent.faith.database.detailcontainer.TreasureChestDatabase
import be.hogent.faith.database.event.EventDatabase
import be.hogent.faith.database.event.EventMapper
import be.hogent.faith.database.event.EventRepository
import be.hogent.faith.database.user.FirebaseUserDatabase
import be.hogent.faith.database.user.UserMapper
import be.hogent.faith.database.user.UserRepository
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.TreasureChest
import be.hogent.faith.service.di.BackpackNames
import be.hogent.faith.service.di.CinemaNames
import be.hogent.faith.service.di.TreasureChestNames
import be.hogent.faith.service.repositories.IAuthManager
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.repositories.IEventRepository
import be.hogent.faith.service.repositories.IUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.qualifier.named
import org.koin.dsl.module

val databaseModule = module {
    single { EventMapper }
    single { DetailMapper }
    single { UserMapper }
    single { DetailContainerMapper }

    single { EventRepository(get()) as IEventRepository }
    single { UserRepository(get(), get()) as IUserRepository }
    single { AuthManager(get()) as IAuthManager }
    single { FirebaseAuthManager(constructFirebaseAuthInstance()) }

    single<IDetailContainerRepository<TreasureChest>>(named(TreasureChestNames.repo)) {
        DetailContainerRepository<TreasureChest>(
            userMapper = get(),
            detailMapper = get(),
            containerMapper = get(),
            database = get(named(TreasureChestNames.database))
        )
    }
    single<IDetailContainerRepository<Backpack>>(named(BackpackNames.repo)) {
        DetailContainerRepository<Backpack>(
            userMapper = get(),
            detailMapper = get(),
            containerMapper = get(),
            database = get(named(BackpackNames.database))
        )
    }
    single<IDetailContainerRepository<Cinema>>(named(CinemaNames.repo)) {
        DetailContainerRepository<Cinema>(
            userMapper = get(),
            detailMapper = get(),
            containerMapper = get(),
            database = get(named(CinemaNames.database))
        )
    }

    single<DetailContainerDatabase<TreasureChest>>(named(TreasureChestNames.database)) {
        TreasureChestDatabase(
            constructFirebaseAuthInstance(),
            constructFireStoreInstance()
        )
    }
    single<DetailContainerDatabase<Backpack>>(named(BackpackNames.database)) {
        BackpackDatabase(
            constructFirebaseAuthInstance(),
            constructFireStoreInstance()
        )
    }
    single<DetailContainerDatabase<Cinema>>(named(CinemaNames.database)) {
        CinemaDatabase(
            constructFirebaseAuthInstance(),
            constructFireStoreInstance()
        )
    }
    single { EventDatabase(constructFirebaseAuthInstance(), constructFireStoreInstance()) }
    single { FirebaseUserDatabase(constructFirebaseAuthInstance(), constructFireStoreInstance()) }
}

fun constructFireStoreInstance(): FirebaseFirestore {
    return FirebaseFirestore.getInstance()
}

fun constructFirebaseAuthInstance(): FirebaseAuth {
    return FirebaseAuth.getInstance()
}
