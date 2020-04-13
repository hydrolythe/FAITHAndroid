package be.hogent.faith.encryption

import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.encryption.internal.DataEncrypter
import be.hogent.faith.service.encryption.EncryptedDetail
import com.google.crypto.tink.KeysetHandle
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
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
                type = dataEncrypter.encrypt(
                    when (detail) {
                        is AudioDetail -> DetailType.Audio
                        is TextDetail -> DetailType.Text
                        is DrawingDetail -> DetailType.Drawing
                        is PhotoDetail -> DetailType.Photo
                        is ExternalVideoDetail -> DetailType.ExternalVideo
                        is YoutubeVideoDetail -> DetailType.YoutubeVideo
                    }.toString()
                ),
                youtubeVideoID = when (detail) {
                    is YoutubeVideoDetail -> dataEncrypter.encrypt(detail.videoId)
                    else -> ""
                }
            )
        )
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
                    uuid = encryptedDetail.uuid
                )
                DetailType.Text -> TextDetail(
                    file = encryptedDetail.file,
                    title = dataEncrypter.decrypt(encryptedDetail.title),
                    uuid = encryptedDetail.uuid
                )
                DetailType.Drawing -> DrawingDetail(
                    file = encryptedDetail.file,
                    title = dataEncrypter.decrypt(encryptedDetail.title),
                    uuid = encryptedDetail.uuid
                )
                DetailType.Photo -> PhotoDetail(
                    file = encryptedDetail.file,
                    title = dataEncrypter.decrypt(encryptedDetail.title),
                    uuid = encryptedDetail.uuid
                )
                DetailType.YoutubeVideo -> YoutubeVideoDetail(
                    file = encryptedDetail.file,
                    title = dataEncrypter.decrypt(encryptedDetail.title),
                    uuid = encryptedDetail.uuid,
                    videoId = encryptedDetail.youtubeVideoID
                )
                DetailType.ExternalVideo -> ExternalVideoDetail(
                    file = encryptedDetail.file,
                    title = dataEncrypter.decrypt(encryptedDetail.title),
                    uuid = encryptedDetail.uuid
                )
            }
        )
    }

    fun decryptDetailFiles(
        encryptedDetail: EncryptedDetail,
        sdek: KeysetHandle
    ): Completable {
        val fileEncrypter = FileEncryptionService()
        // YoutubeVideos don't have a file that needs o be encrypted
        if (encryptedDetail.youtubeVideoID.isNotEmpty()) {
            return Completable.complete()
        } else {
            return fileEncrypter.decrypt(encryptedDetail.file, sdek)
                .map { encryptedDetail.file = it }
                .ignoreElement()
        }
    }
}

internal enum class DetailType {
    Audio,
    Text,
    Drawing,
    Photo,
    ExternalVideo,
    YoutubeVideo
}