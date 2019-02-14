package be.hogent.faith.storage.di

import be.hogent.faith.storage.StorageRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val storageModule = module {
    factory { StorageRepositoryImpl(androidContext()) }
}
