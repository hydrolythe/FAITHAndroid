package be.hogent.faith.encryption

import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.VideoDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.encryption.internal.DataEncrypter
import be.hogent.faith.service.encryption.EncryptedDetail
import com.google.crypto.tink.KeysetHandle
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.zipWith
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import java.io.File

class DetailEncryptionService(
    private val fileEncryptionService: FileEncryptionService
) {

    /**
     * Returns an encrypted version of the [Detail], both its data and its file.
     */
    fun encrypt(detail: Detail, dek: KeysetHandle, sdek: KeysetHandle): Single<EncryptedDetail> {
        return if (detail is YoutubeVideoDetail) {
            encryptData(detail, dek)
        } else {
            encryptData(detail, dek)
                .zipWith(encryptDetailFiles(detail, sdek)) { encryptedDetail, file ->
                    Timber.i("Encrypting file. Original detail file: ${detail.file.path}")
                    Timber.i("Encrypting file. New detail file: ${file.path}")
                    encryptedDetail.file = file
                    encryptedDetail
                }
        }
    }

    private fun encryptDetailFiles(detail: Detail, sdek: KeysetHandle): Single<File> {
        return fileEncryptionService.encrypt(detail.file, sdek)
            .doOnSuccess { Timber.i("Encrypted detail files for detail ${detail.uuid}") }
    }

    /**
     * Encrypts the data of  the detail.
     * Does  not set the [EncryptedDetail.file] to an en encrypted version of the file!
     * This should be done afterwards, once the file has been encrypted.
     */
    private fun encryptData(detail: Detail, dek: KeysetHandle): Single<EncryptedDetail> {
        val dataEncrypter = DataEncrypter(dek)
        return Single.just(
            EncryptedDetail(
                file = detail.file,
                uuid = detail.uuid,
                title = dataEncrypter.encrypt(detail.title),
                dateTime = dataEncrypter.encrypt(detail.dateTime.toString()),
                type = dataEncrypter.encrypt(
                    when (detail) {
                        is AudioDetail -> DetailType.Audio
                        is TextDetail -> DetailType.Text
                        is DrawingDetail -> DetailType.Drawing
                        is PhotoDetail -> DetailType.Photo
                        is VideoDetail -> DetailType.ExternalVideo
                        is YoutubeVideoDetail -> DetailType.YoutubeVideo
                        is FilmDetail -> DetailType.Film
                    }.toString()
                ),
                thumbnail = detail.thumbnail?.let { dataEncrypter.encrypt(it) },
                youtubeVideoID = when (detail) {
                    is YoutubeVideoDetail -> detail.videoId
                    else -> ""
                }
            )
        )
            .doOnSuccess { Timber.i("Encrypted detail data for detail ${detail.uuid}") }
    }

    /**
     * Decrypts the data of the [encryptedDetail], resulting in a regular [Detail].
     * The file of the [encryptedDetail] will **not** be decrypted.
     */
    fun decryptData(
        encryptedDetail: EncryptedDetail,
        dek: KeysetHandle
    ): Single<Detail> {
        val dataEncrypter = DataEncrypter(dek)
        val detailTypeString = dataEncrypter.decrypt(encryptedDetail.type)

        return Single.just(
            when (DetailType.valueOf(detailTypeString)) {
                DetailType.Audio -> AudioDetail(
                    file = encryptedDetail.file,
                    title = dataEncrypter.decrypt(encryptedDetail.title),
                    uuid = encryptedDetail.uuid,
                    dateTime = LocalDateTime.parse(dataEncrypter.decrypt(encryptedDetail.dateTime))
                )
                DetailType.Text -> TextDetail(
                    file = encryptedDetail.file,
                    title = dataEncrypter.decrypt(encryptedDetail.title),
                    uuid = encryptedDetail.uuid,
                    dateTime = LocalDateTime.parse(dataEncrypter.decrypt(encryptedDetail.dateTime))
                )
                DetailType.Drawing -> DrawingDetail(
                    file = encryptedDetail.file,
                    title = dataEncrypter.decrypt(encryptedDetail.title),
                    uuid = encryptedDetail.uuid,
                    dateTime = LocalDateTime.parse(dataEncrypter.decrypt(encryptedDetail.dateTime)),
                    thumbnail = encryptedDetail.thumbnail?.let { dataEncrypter.decrypt(it) }
                )
                DetailType.Photo -> PhotoDetail(
                    file = encryptedDetail.file,
                    title = dataEncrypter.decrypt(encryptedDetail.title),
                    uuid = encryptedDetail.uuid,
                    dateTime = LocalDateTime.parse(dataEncrypter.decrypt(encryptedDetail.dateTime)),
                    thumbnail = encryptedDetail.thumbnail?.let { dataEncrypter.decrypt(it) }
                )
                DetailType.YoutubeVideo -> YoutubeVideoDetail(
                    file = encryptedDetail.file,
                    title = dataEncrypter.decrypt(encryptedDetail.title),
                    uuid = encryptedDetail.uuid,
                    dateTime = LocalDateTime.parse(dataEncrypter.decrypt(encryptedDetail.dateTime)),
                    videoId = encryptedDetail.youtubeVideoID
                )
                DetailType.ExternalVideo -> VideoDetail(
                    file = encryptedDetail.file,
                    title = dataEncrypter.decrypt(encryptedDetail.title),
                    uuid = encryptedDetail.uuid,
                    dateTime = LocalDateTime.parse(dataEncrypter.decrypt(encryptedDetail.dateTime)),
                    thumbnail = encryptedDetail.thumbnail?.let { dataEncrypter.decrypt(it) }
                )
                DetailType.Film -> FilmDetail(
                    file = encryptedDetail.file,
                    title = dataEncrypter.decrypt(encryptedDetail.title),
                    uuid = encryptedDetail.uuid,
                    dateTime = LocalDateTime.parse(dataEncrypter.decrypt(encryptedDetail.dateTime)),
                    thumbnail = encryptedDetail.thumbnail?.let { dataEncrypter.decrypt(it) }
                )
            }
        )
            .doOnSuccess { Timber.i("Decrypted detail data for detail ${encryptedDetail.uuid}") }
    }

    /**
     * Decrypts the file in a [detail].
     * The file of the detail will be updated to reflect the decrypted file.
     */
    fun decryptDetailFile(
        detail: Detail,
        sdek: KeysetHandle
    ): Completable {
        return Completable.defer {
            // YoutubeVideos don't have a file that needs to be encrypted
            if (detail is YoutubeVideoDetail) {
                Completable.complete()
                    .doOnComplete { Timber.i("Detail ${detail.uuid} is a YoutubeVideoDetail, nothing to encrypt") }
            } else {
                val fileEncrypter = FileEncryptionService()
                fileEncrypter.decrypt(detail.file, sdek)
                    .flatMapCompletable {
                        Completable.fromAction {
                            Timber.i("Decrypted file for detail ${detail.uuid} is ${it.path}")
                            detail.file = it
                        }
                    }
            }
        }
    }

    /**
     * Decrypts the file belonging to a detail, and places it in the given [destinationFile].
     */
    fun decryptDetailFile(
        file: File,
        sdek: KeysetHandle,
        destinationFile: File
    ): Completable {
        return FileEncryptionService().decrypt(file, sdek, destinationFile)
    }

    /**
     * Decrypts the file belonging to a detail, and places it in the given [destinationFile].
     */
    fun decryptDetailFile(
        detail: Detail,
        sdek: KeysetHandle,
        destinationFile: File
    ): Completable {
        return Completable.defer {
            if (detail is YoutubeVideoDetail) {
                Completable
                    .complete()
                    .doOnComplete { Timber.i("Detail ${detail.uuid} is a YoutubeVideoDetail, nothing to encrypt") }
            } else {
                decryptDetailFile(detail.file, sdek, destinationFile)
                    .doOnComplete { Timber.i("Decrypted file for detail ${detail.uuid} to ${destinationFile.path}") }
            }
        }
    }

    /**
     * Decrypts the file belonging to a detail, and places it in the given [destinationFile].
     */
    fun decryptDetailFile(
        encryptedDetail: EncryptedDetail,
        sdek: KeysetHandle,
        destinationFile: File
    ): Completable {
        return Completable.defer {
            if (encryptedDetail.youtubeVideoID.isNotEmpty()) {
                Completable
                    .complete()
                    .doOnComplete { Timber.i("Detail ${encryptedDetail.uuid} is a YoutubeVideoDetail, nothing to encrypt") }
            } else {
                decryptDetailFile(encryptedDetail.file, sdek, destinationFile)
                    .doOnComplete { Timber.i("Decrypted file for detail ${encryptedDetail.uuid} to ${destinationFile.path}") }
            }
        }
    }

    internal enum class DetailType {
        Audio,
        Text,
        Drawing,
        Photo,
        ExternalVideo,
        YoutubeVideo,
        Film
    }
}