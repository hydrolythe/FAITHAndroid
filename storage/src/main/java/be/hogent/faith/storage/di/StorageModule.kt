package be.hogent.faith.storage.di

import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.storage.FileStorageRepository
import be.hogent.faith.storage.StoragePathProvider
import be.hogent.faith.storage.local.ILocalFileStorageRepository
import be.hogent.faith.storage.local.ITemporaryFileStorageRepository
import be.hogent.faith.storage.local.LocalFileStorageRepository
import be.hogent.faith.storage.local.TemporaryStorageRepository
import be.hogent.faith.storage.online.FirebaseStorageRepository
import be.hogent.faith.storage.online.IOnlineFileStorageRepository
import be.hogent.faith.storage.online.RxFirebaseStorageWrapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val storageModule = module {
    factory { FileStorageRepository(get(), get(), get()) as IFileStorageRepository }
    factory { StoragePathProvider(androidContext(), constructFirebaseAuthInstance()) }
    factory { TemporaryStorageRepository(androidContext(), get()) as be.hogent.faith.service.repositories.ITemporaryFileStorageRepository }
    factory { TemporaryStorageRepository(androidContext(), get()) as ITemporaryFileStorageRepository }
    factory<ILocalFileStorageRepository> {
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
