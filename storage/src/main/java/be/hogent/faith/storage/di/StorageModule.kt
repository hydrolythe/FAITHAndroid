package be.hogent.faith.storage.di

import be.hogent.faith.storage.StoragePathProvider
import be.hogent.faith.storage.StorageRepository
import be.hogent.faith.storage.localStorage.LocalStorageRepository
import be.hogent.faith.storage.localStorage.ITemporaryStorage
import be.hogent.faith.storage.firebase.FireBaseStorageRepository
import be.hogent.faith.storage.localStorage.TemporaryStorageRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val storageModule = module {
    factory { StorageRepository(get(), get()) }
    factory { StoragePathProvider(androidContext(), constructFirebaseAuthInstance()) }
    factory { LocalStorageRepository(get(), androidContext()) }
    factory { FireBaseStorageRepository(get(), constructFirebaseStorageInstance()) }
    factory<ITemporaryStorage> { TemporaryStorageRepository(androidContext(), get()) }
}

fun constructFirebaseStorageInstance(): FirebaseStorage {
    return FirebaseStorage.getInstance()
}

fun constructFirebaseAuthInstance(): FirebaseAuth {
    return FirebaseAuth.getInstance()
}
