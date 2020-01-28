package be.hogent.faith.storage.di

import be.hogent.faith.storage.IFileStorageRepository
import be.hogent.faith.storage.StoragePathProvider
import be.hogent.faith.storage.FileStorageRepository
import be.hogent.faith.storage.firebase.OnlineFileStorageRepository
import be.hogent.faith.storage.firebase.IOnlineFileStorageRepository
import be.hogent.faith.storage.localStorage.ITemporaryStorage
import be.hogent.faith.storage.localStorage.LocalFileStorageRepository
import be.hogent.faith.storage.localStorage.TemporaryStorageRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val storageModule = module {
    factory<IFileStorageRepository> { FileStorageRepository(get(), get()) }
    factory { StoragePathProvider(androidContext(), constructFirebaseAuthInstance()) }
    factory<ITemporaryStorage> { TemporaryStorageRepository(androidContext(), get()) }
    factory<be.hogent.faith.database.storage.ILocalFileStorageRepository> { LocalFileStorageRepository(get(), get(), androidContext()) }
    factory<IOnlineFileStorageRepository> {
        OnlineFileStorageRepository(
            get(),
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
