package be.hogent.faith.storage.localStorage

import android.content.Context
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.IStorage
import be.hogent.faith.storage.StoragePathProvider
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

const val EMOTION_AVATAR_FILENAME = "emotionAvatar"

class LocalStorageRepository(
    private val pathProvider: StoragePathProvider,
    private val context: Context
) : IStorage {
    override fun saveEvent(event: Event): Completable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getFile(detail: Detail): Single<File> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getEmotionAvatar(event: Event): Single<File> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
