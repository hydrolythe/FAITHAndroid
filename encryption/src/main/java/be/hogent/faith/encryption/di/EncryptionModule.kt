package be.hogent.faith.encryption.di

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.encryption.DetailContainerEncryptionService
import be.hogent.faith.encryption.DetailEncryptionService
import be.hogent.faith.encryption.EventEncryptionService
import be.hogent.faith.encryption.FileEncryptionService
import be.hogent.faith.encryption.internal.ENDPOINT
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyEncryptionService
import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.encryption.IEventEncryptionService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

val encryptionModule = module {
    single { provideMoshi() }
    single { provideKeyEncryptionService(get()) }
    single { KeyEncrypter(get()) }
    single { KeyGenerator() }
    single { FileEncryptionService() }
    single { DetailEncryptionService(get()) }
    factory<IEventEncryptionService> { EventEncryptionService(get(), get(), get(), get()) }
    factory<IDetailContainerEncryptionService<Backpack>> {
        DetailContainerEncryptionService<Backpack>(get(), get(), get())
    }
    factory<IDetailContainerEncryptionService<Backpack>>(named("BackpackEncryptionService")) {
        DetailContainerEncryptionService<Backpack>(get(), get(), get())
    }
    factory<IDetailContainerEncryptionService<Cinema>>(named("CinemaEncryptionService")) {
        DetailContainerEncryptionService<Cinema>(get(), get(), get())
    }
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
