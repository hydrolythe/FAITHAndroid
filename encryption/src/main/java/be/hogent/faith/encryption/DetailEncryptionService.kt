package be.hogent.faith.encryption

import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.encryption.internal.DataEncrypter
import be.hogent.faith.service.encryption.EncryptedDetail
import com.google.crypto.tink.KeysetHandle
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import java.io.File

class DetailEncryptionService(
    private val fileEncryptionService: FileEncryptionService
) {

    /**
     * Encrypts both the data within a file and the files associated with it.
     */
    fun encrypt(detail: Detail, dek: KeysetHandle, sdek: KeysetHandle): Single<EncryptedDetail> {
        return encryptData(detail, dek).zipWith(
            encryptDetailFiles(detail, sdek)
        ) { encryptedDetail, file ->
            encryptedDetail.file = file
            encryptedDetail
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
                        is ExternalVideoDetail -> DetailType.ExternalVideo
                        is YoutubeVideoDetail -> DetailType.YoutubeVideo
                        is FilmDetail -> DetailType.Film
                    }.toString()
                ),
                youtubeVideoID = when (detail) {
                    is YoutubeVideoDetail -> dataEncrypter.encrypt(detail.videoId)
                    else -> ""
                }
            )
        )
            .doOnSuccess { Timber.i("Encrypted detail data for detail ${detail.uuid}") }
    }

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
                    dateTime = LocalDateTime.parse(dataEncrypter.decrypt(encryptedDetail.dateTime))
                )
                DetailType.Photo -> PhotoDetail(
                    file = encryptedDetail.file,
                    title = dataEncrypter.decrypt(encryptedDetail.title),
                    uuid = encryptedDetail.uuid,
                    dateTime = LocalDateTime.parse(dataEncrypter.decrypt(encryptedDetail.dateTime))
                )
                DetailType.YoutubeVideo -> YoutubeVideoDetail(
                    file = encryptedDetail.file,
                    title = dataEncrypter.decrypt(encryptedDetail.title),
                    uuid = encryptedDetail.uuid,
                    dateTime = LocalDateTime.parse(dataEncrypter.decrypt(encryptedDetail.dateTime)),
                    videoId = encryptedDetail.youtubeVideoID
                )
                DetailType.ExternalVideo -> ExternalVideoDetail(
                    file = encryptedDetail.file,
                    title = dataEncrypter.decrypt(encryptedDetail.title),
                    uuid = encryptedDetail.uuid,
                    dateTime = LocalDateTime.parse(dataEncrypter.decrypt(encryptedDetail.dateTime))
                )
                DetailType.Film -> FilmDetail(
                    file = encryptedDetail.file,
                    title = dataEncrypter.decrypt(encryptedDetail.title),
                    uuid = encryptedDetail.uuid,
                    dateTime = LocalDateTime.parse(dataEncrypter.decrypt(encryptedDetail.dateTime))
                )
            }
        )
            .doOnSuccess { Timber.i("Decrypted detail data for detail ${encryptedDetail.uuid}") }
    }

    fun decryptDetailFiles(
        detail: Detail,
        sdek: KeysetHandle
    ): Completable {
        val fileEncrypter = FileEncryptionService()
        // YoutubeVideos don't have a file that needs to be encrypted
        if (detail is YoutubeVideoDetail) {
            return Completable.complete()
                .doOnComplete { Timber.i("Detail ${detail.uuid} is a YoutubeVideoDetail, nothing to encrypt") }
        } else {
            return fileEncrypter.decrypt(detail.file, sdek)
                .map { detail.file = it }
                .ignoreElement()
                .doOnComplete { Timber.i("Decrypted detail files for detail ${detail.uuid}") }
        }
    }

    fun decryptDetailFiles(
        encryptedDetail: EncryptedDetail,
        sdek: KeysetHandle
    ): Completable {
        val fileEncrypter = FileEncryptionService()
        // YoutubeVideos don't have a file that needs to be encrypted
        if (encryptedDetail.youtubeVideoID.isNotEmpty()) {
            return Completable.complete()
                .doOnComplete { Timber.i("Detail ${encryptedDetail.uuid} is a YoutubeVideoDetail, nothing to encrypt") }
        } else {
            return fileEncrypter.decrypt(encryptedDetail.file, sdek)
                .map { encryptedDetail.file = it }
                .ignoreElement()
                .doOnComplete { Timber.i("Decrypted detail files for detail ${encryptedDetail.uuid}") }
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