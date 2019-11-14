package be.hogent.faith.storage.storage

class StorageFactory(
    private val localStorage: LocalStorage,
    private val remoteStorage: RemoteStorage
) {

    fun getDataStore(cached: Boolean): IStorage {

        return if (cached) {
            localStorage
        } else {
            remoteStorage
        }
    }

    fun getCacheStorage(): LocalStorage {
        return localStorage
    }

    fun getRemoteDataStore(): RemoteStorage {
        return remoteStorage
    }
}