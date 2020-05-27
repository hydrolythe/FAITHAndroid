package be.hogent.faith.storage.online

import android.net.Uri
import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.service.encryption.EncryptedDetail
import be.hogent.faith.service.encryption.EncryptedEvent
import be.hogent.faith.storage.StoragePathProvider
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.toFlowable
import timber.log.Timber
import java.io.File

class FirebaseStorageRepository(
    private val pathProvider: StoragePathProvider,
    private val firestorage: FirebaseStorage,
    private val rxFirebaseStorage: IRxFireBaseStorage
) : IOnlineFileStorageRepository {

    private var storageRef = firestorage.reference

    /**
     * Saves all files associated with an [EncryptedEventEntity] (detail files, emotionavatar)
     */
    override fun saveEventFiles(encryptedEvent: EncryptedEvent): Completable {
        // TODO: do all the downloading in parallel using merge instead of concat
        return saveEventEmotionAvatar(encryptedEvent) // save avatar in firestorage
            .concatWith(
                encryptedEvent.details.toFlowable() // save all detail files in firestorage
                    .concatMapCompletable {
                        saveDetailFile(encryptedEvent, it)
                    }
            )
    }

    /**
     * Uploads the emotion avatar file to [firestorage]
     */
    private fun saveEventEmotionAvatar(encryptedEvent: EncryptedEvent): Completable {
        if (encryptedEvent.emotionAvatar == null) {
            return Completable.complete()
                .doOnComplete { Timber.i("No emotionAvatar to save online for ${encryptedEvent.uuid}") }
        } else {
            return rxFirebaseStorage
                .putFile(
                    storageRef.child(pathProvider.emotionAvatarPath(encryptedEvent).path),
                    Uri.parse("file://${encryptedEvent.emotionAvatar!!.path}")
                )
                .ignoreElement()
                .doOnComplete { Timber.i("Saved emotionavatar online for ${encryptedEvent.uuid}") }
        }
    }

    /**
     * Uploads the detail file to [firestorage]
     */
    private fun saveDetailFile(event: EncryptedEvent, detail: EncryptedDetail): Completable {
        // YoutubeVideoDetails don't have a "real" file
        if (detail.youtubeVideoID.isNotEmpty()) {
            return Completable.complete()
        } else {
            return rxFirebaseStorage
                .putFile(
                    storageRef.child(pathProvider.detailPath(detail, event).path),
                    Uri.parse("file://${detail.file.path}")
                )
                .ignoreElement()
                .doOnComplete { Timber.i("Saved details online for event ${event.uuid}") }
        }
    }

    /**
     * Downloads a detail's file from [firestorage] and stores it into the path provided for it by the
     * [pathProvider].
     */
    override fun downloadDetail(detail: Detail, event: Event): Completable {
        // YoutubeVideoDetails don't have a "real" file
        if (detail is YoutubeVideoDetail) {
            return Completable.complete()
        } else {
            val localDestinationFile: File =
                with(pathProvider) { localStorage(detailPath(detail, event)) }
            localDestinationFile.parentFile.mkdirs()
            return Completable
                .fromSingle(
                    rxFirebaseStorage.getFile(
                        storageRef.child(pathProvider.detailPath(detail, event).path),
                        localDestinationFile
                    )
                        .doOnSuccess { "Downloaded file for detail ${detail.uuid} in event ${event.uuid}" }
                )
                .doOnError { Timber.e("Error while downloading detail ${detail.uuid} in event ${event.uuid}") }
                .andThen {
                    detail.file = localDestinationFile
                }
        }
    }

    /**
     * Downloads a detail's file from [firestorage] and stores it into the path provided for it by the
     * [pathProvider].
     */
    override fun downloadDetail(detail: Detail, container: DetailsContainer): Completable {
        // YoutubeVideoDetails don't have a "real" file
        if (detail is YoutubeVideoDetail) {
            return Completable.complete()
        } else {
            val localDestinationFile: File =
                with(pathProvider) { localStorage(detailPath(detail, container)) }
            localDestinationFile.parentFile.mkdirs()
            return Completable
                .fromSingle(
                    rxFirebaseStorage.getFile(
                        storageRef.child(pathProvider.detailPath(detail, container).path),
                        localDestinationFile
                    )
                )
                .andThen {
                    detail.file = localDestinationFile
                }
                .doOnComplete { "Downloaded file for detail ${detail.uuid} in ${container.javaClass}" }
        }
    }

    /**
     * Gets the emotion avatar file from [firestorage] and stores it into the path provided for it
     * by the [pathProvider].
     */
    override fun downloadEmotionAvatar(event: Event): Completable {
        if (event.emotionAvatar == null) {
            return Completable.complete()
        } else {
            val localDestinationFile = with(pathProvider) { localStorage(emotionAvatarPath(event)) }
            localDestinationFile.parentFile.mkdirs()
            return rxFirebaseStorage
                .getFile(
                    storageRef.child(pathProvider.emotionAvatarPath(event).path),
                    localDestinationFile
                )
                .doOnError { Timber.e("Error while downloading emotionAvatar for event ${event.uuid}") }
                .ignoreElement()
                .andThen {
                    event.emotionAvatar = localDestinationFile
                }
                .doOnComplete { "Downloaded emotionAvatar for event ${event.uuid}" }
        }
    }

    override fun saveDetailFiles(
        encryptedDetail: EncryptedDetail,
        container: DetailsContainer
    ): Completable {
        // YoutubeVideoDetails don't have a "real" file
        if (encryptedDetail.youtubeVideoID.isNotEmpty()) {
            return Completable.complete()
        } else {
            return rxFirebaseStorage
                .putFile(
                    storageRef.child(pathProvider.detailPath(encryptedDetail, container).path),
                    Uri.parse("file://${encryptedDetail.file.path}")
                )
                .ignoreElement()
                .doOnComplete { "Saved detail files for ${encryptedDetail.uuid} in ${container.javaClass}" }
        }
    }

    override fun deleteFiles(event: Event) : Completable{
        // Can't just delete the directory because firebasestorage doesn't _really_ use directories,
        // just paths.
        return Observable.fromIterable(event.details)
            .flatMapCompletable{ detail ->
                rxFirebaseStorage.delete(
                    storageRef.child(pathProvider.detailPath(detail, event).path)
                ).doOnComplete { Timber.i("Deleted detail ${detail.uuid} from event") }
            }
    }

    override fun deleteFiles(detail: Detail, container: DetailsContainer): Completable {
        val refToFile = storageRef.child(pathProvider.detailPath(detail, container).path)
        return rxFirebaseStorage.delete(refToFile)
    }
}
