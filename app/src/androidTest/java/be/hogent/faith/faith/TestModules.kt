package be.hogent.faith.faith

import be.hogent.faith.domain.repository.AuthManager
import be.hogent.faith.domain.repository.IEventRepository
import be.hogent.faith.domain.repository.UserRepository
import be.hogent.faith.storage.IStorageRepository
import be.hogent.faith.storage.localStorage.ITemporaryStorage
import org.koin.dsl.module

val androidTestModule = module(override = true) {
    single { TestAuthManager() as AuthManager }
    single { TestUserRepository() as UserRepository }
    single { TestEventRepository() as IEventRepository }
    factory<IStorageRepository> { TestStorageRepository() }
    factory<ITemporaryStorage> { TestITemporyStorage() }
}