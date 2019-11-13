package be.hogent.faith.storage.storage

class StorageFactory(
    private val cacheStorage: CacheStorage,
    private val remoteStorage: RemoteStorage
) {

    fun getDataStore(cached: Boolean): IStorage {

        return if (cached) {
            cacheStorage
        } else {
            remoteStorage
        }
    }

    fun getCacheStorage(): CacheStorage {
        return cacheStorage
    }

    fun getRemoteDataStore(): RemoteStorage {
        return remoteStorage
    }
}