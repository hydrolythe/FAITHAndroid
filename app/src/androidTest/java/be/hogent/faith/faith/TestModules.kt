package be.hogent.faith.faith

import be.hogent.faith.domain.repository.AuthManager
import be.hogent.faith.domain.repository.EventRepository
import be.hogent.faith.domain.repository.UserRepository
import be.hogent.faith.storage.IStorageRepository
import be.hogent.faith.storage.localStorage.ITemporaryStorage
import org.koin.dsl.module

val androidTestModule = module(override = true) {
    single { TestAuthManager() as AuthManager }
    single { TestUserRepository() as UserRepository }
    single { TestEventRepository() as EventRepository }
    factory<IStorageRepository> { TestStorageRepository() }
    factory<ITemporaryStorage> { TestITemporyStorage() }

    /**
     *  Meer op https://github.com/InsertKoinIO/koin/issues/450
     *scope(named(KoinModules.USER_SCOPE_NAME)) {
     *    scoped { scope.declareMock<UserViewModel>()} //UserViewModel(get(), get()) }
     * }
     */
}