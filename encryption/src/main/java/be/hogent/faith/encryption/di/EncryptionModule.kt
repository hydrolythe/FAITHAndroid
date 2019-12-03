package be.hogent.faith.encryption.di

import be.hogent.faith.database.encryption.IDetailEntityEncrypter
import be.hogent.faith.database.encryption.IEventEntityEncryptor
import be.hogent.faith.encryption.DetailEntityEncrypter
import be.hogent.faith.encryption.Encrypter
import be.hogent.faith.encryption.EventEntityEncryptor
import be.hogent.faith.encryption.FileEncryptor
import be.hogent.faith.storage.encryption.IFileEncryptor
import org.koin.dsl.module

val encryptionModule = module {
    single { Encrypter() }
    factory { DetailEntityEncrypter(get()) as IDetailEntityEncrypter }
    factory { EventEntityEncryptor(get(), get()) as IEventEntityEncryptor }
    factory { FileEncryptor(get()) as IFileEncryptor }
}
