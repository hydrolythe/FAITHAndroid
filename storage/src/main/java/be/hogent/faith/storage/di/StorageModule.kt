package be.hogent.faith.storage.di

import be.hogent.faith.database.storage.IFileStorageRepository
import be.hogent.faith.storage.FileStorageRepository
import be.hogent.faith.storage.StoragePathProvider
import be.hogent.faith.storage.firebase.FirebaseStorageRepository
import be.hogent.faith.storage.firebase.IOnlineFileStorageRepository
import be.hogent.faith.storage.firebase.RxFirebaseStorageWrapper
import be.hogent.faith.storage.localStorage.ITemporaryStorageRepository
import be.hogent.faith.storage.localStorage.LocalFileStorageRepository
import be.hogent.faith.storage.localStorage.TemporaryStorageRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val storageModule = module {
    factory<IFileStorageRepository> { FileStorageRepository(get(), get(), get()) }
    factory { StoragePathProvider(androidContext(), constructFirebaseAuthInstance()) }
    factory<ITemporaryStorageRepository> { TemporaryStorageRepository(androidContext(), get()) }
    factory<be.hogent.faith.database.storage.ILocalFileStorageRepository> {
        LocalFileStorageRepository(get())
    }
    factory<IOnlineFileStorageRepository> {
        FirebaseStorageRepository(
            get(),
            constructFirebaseStorageInstance(),
            RxFirebaseStorageWrapper()
        )
    }
}

fun constructFirebaseStorageInstance(): FirebaseStorage {
    return FirebaseStorage.getInstance()
}

fun constructFirebaseAuthInstance(): FirebaseAuth {
    return FirebaseAuth.getInstance()
}
