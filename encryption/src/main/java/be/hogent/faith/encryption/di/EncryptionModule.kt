package be.hogent.faith.encryption.di

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.TreasureChest
import be.hogent.faith.encryption.DetailContainerEncryptionService
import be.hogent.faith.encryption.DetailEncryptionService
import be.hogent.faith.encryption.EventEncryptionService
import be.hogent.faith.encryption.FileEncryptionService
import be.hogent.faith.encryption.GoalEncryptionService
import be.hogent.faith.encryption.SubGoalEncryptionService
import be.hogent.faith.encryption.ActionEncryptionService
import be.hogent.faith.encryption.internal.ENDPOINT
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyEncryptionService
import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.service.di.BackpackNames
import be.hogent.faith.service.di.CinemaNames
import be.hogent.faith.service.di.TreasureChestNames
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import be.hogent.faith.service.encryption.IEventEncryptionService
import be.hogent.faith.service.encryption.IGoalEncryptionService
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
    single { DetailEncryptionService(get(), get()) }
    factory<IEventEncryptionService> { EventEncryptionService(get(), get(), get(), get(), get()) }
    factory<IDetailContainerEncryptionService<Backpack>>(named(BackpackNames.encryptionService)) {
        DetailContainerEncryptionService<Backpack>(get(), get(), get(), get())
    }
    factory<IDetailContainerEncryptionService<TreasureChest>>(named(TreasureChestNames.encryptionService)) {
        DetailContainerEncryptionService<TreasureChest>(get(), get(), get(), get())
    }
    factory<IDetailContainerEncryptionService<Cinema>>(named(CinemaNames.encryptionService)) {
        DetailContainerEncryptionService<Cinema>(get(), get(), get(), get())
    }
    factory<IGoalEncryptionService> { GoalEncryptionService(get(), get(), get()) }
    single { SubGoalEncryptionService(get()) }
    single { ActionEncryptionService() }
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
