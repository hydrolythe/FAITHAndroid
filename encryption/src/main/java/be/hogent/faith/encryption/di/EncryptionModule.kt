package be.hogent.faith.encryption.di

import be.hogent.faith.database.encryption.IDetailEntityEncrypter
import be.hogent.faith.database.encryption.IEventEntityEncrypter
import be.hogent.faith.encryption.DetailEntityEncrypter
import be.hogent.faith.encryption.EventEntityEncrypter
import be.hogent.faith.encryption.FileEncrypter
import be.hogent.faith.encryption.encryptionService.ENDPOINT
import be.hogent.faith.encryption.encryptionService.KeyEncryptionService
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.storage.encryption.IFileEncrypter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val encryptionModule = module {
    single { provideMoshi() }
    single { provideKeyEncryptionService(get()) }
    single { KeyEncrypter(get()) }
    single { KeyGenerator() }
    factory { DetailEntityEncrypter(get()) as IDetailEntityEncrypter }
    factory { EventEntityEncrypter(get(), get()) as IEventEntityEncrypter }
    factory { FileEncrypter(get()) as IFileEncrypter }
}

fun provideMoshi(): Moshi {
    return Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}

fun provideKeyEncryptionService(moshi: Moshi): KeyEncryptionService {
    return Retrofit.Builder()
        .baseUrl(ENDPOINT)
        .addConverterFactory(
            MoshiConverterFactory.create(moshi)
        )
        .build().create(KeyEncryptionService::class.java)
}
