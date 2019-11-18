package be.hogent.faith.storage.di

import be.hogent.faith.storage.StorageRepository
import be.hogent.faith.storage.localStorage.LocalStorageImpl
import be.hogent.faith.storage.localStorage.ILocalStorage
import be.hogent.faith.storage.firebase.FirebaseStorageImpl
import be.hogent.faith.storage.firebase.IFirebaseStorage
import be.hogent.faith.storage.storage.LocalStorage
import be.hogent.faith.storage.storage.RemoteStorage
import be.hogent.faith.storage.storage.StorageFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val storageModule = module {
    factory { StorageRepository(get()) }
    factory { StorageFactory(get(), get()) }
    factory { RemoteStorage(get()) }
    factory { LocalStorage(get()) }
    factory<ILocalStorage> { LocalStorageImpl(androidContext()) }
    single<IFirebaseStorage> {
        FirebaseStorageImpl(
            constructFirebaseAuthInstance(),
            constructFirebaseStorageInstance()
        )
    }
}

fun constructFirebaseStorageInstance(): FirebaseStorage {
    return FirebaseStorage.getInstance()
}

fun constructFirebaseAuthInstance(): FirebaseAuth {
    return FirebaseAuth.getInstance()
}

