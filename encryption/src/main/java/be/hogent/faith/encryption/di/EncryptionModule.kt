package be.hogent.faith.encryption.di

import be.hogent.faith.service.usecases.encryption.IEventEncryptionService
import be.hogent.faith.encryption.DetailEncryptionService
import be.hogent.faith.encryption.EventEncryptionService
import be.hogent.faith.encryption.encryptionService.ENDPOINT
import be.hogent.faith.encryption.encryptionService.KeyEncryptionService
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

val encryptionModule = module {
    single { provideMoshi() }
    single { provideKeyEncryptionService(get()) }
    single { KeyEncrypter(get()) }
    single { KeyGenerator() }
    single { DetailEncryptionService() }
    factory { EventEncryptionService(get(), get()) as be.hogent.faith.service.usecases.encryption.IEventEncryptionService }
}

fun provideMoshi(): Moshi {
    return Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}

fun provideKeyEncryptionService(moshi: Moshi): KeyEncryptionService {
    return Retrofit.Builder()
        .baseUrl(ENDPOINT)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(
            MoshiConverterFactory.create(moshi)
        )
        .build().create(KeyEncryptionService::class.java)
}
