package be.hogent.faith.storage.di

import be.hogent.faith.storage.IStorageRepository
import be.hogent.faith.storage.StoragePathProvider
import be.hogent.faith.storage.StorageRepository
import be.hogent.faith.storage.localStorage.LocalStorageRepository
import be.hogent.faith.storage.localStorage.ITemporaryStorage
import be.hogent.faith.storage.firebase.FireBaseStorageRepository
import be.hogent.faith.storage.firebase.IFireBaseStorageRepository
import be.hogent.faith.storage.localStorage.ILocalStorageRepository
import be.hogent.faith.storage.localStorage.TemporaryStorageRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val storageModule = module {
    factory<IStorageRepository> { StorageRepository(get(), get(), get()) }
    factory { StoragePathProvider(androidContext(), constructFirebaseAuthInstance()) }
    factory<ITemporaryStorage> { TemporaryStorageRepository(androidContext(), get()) }
    factory<ILocalStorageRepository> { LocalStorageRepository(get(), androidContext()) }
    factory<IFireBaseStorageRepository> { FireBaseStorageRepository(get(), constructFirebaseStorageInstance()) }
}

fun constructFirebaseStorageInstance(): FirebaseStorage {
    return FirebaseStorage.getInstance()
}

fun constructFirebaseAuthInstance(): FirebaseAuth {
    return FirebaseAuth.getInstance()
}
