package be.hogent.faith.encryption

import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.VideoDetail
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
                title = detail.title,
                type = dataEncrypter.encrypt(
                    when (detail) {
                        is AudioDetail -> DetailType.Audio
                        is TextDetail -> DetailType.Text
                        is DrawingDetail -> DetailType.Drawing
                        is PhotoDetail -> DetailType.Photo
                        is VideoDetail -> DetailType.Video
                        is ExternalVideoDetail -> DetailType.ExternalVideo
                    }.toString()
                )
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
                    encryptedDetail.file,
                    encryptedDetail.title,
                    encryptedDetail.uuid
                )
                DetailType.Text -> TextDetail(
                    encryptedDetail.file,
                    encryptedDetail.title,
                    encryptedDetail.uuid
                )
                DetailType.Drawing -> DrawingDetail(
                    encryptedDetail.file,
                    encryptedDetail.title,
                    encryptedDetail.uuid
                )
                DetailType.Photo -> PhotoDetail(
                    encryptedDetail.file,
                    encryptedDetail.title,
                    encryptedDetail.uuid
                )
                DetailType.Video -> VideoDetail(
                    encryptedDetail.file,
                    encryptedDetail.title,
                    encryptedDetail.uuid
                )
                DetailType.ExternalVideo -> ExternalVideoDetail(
                    encryptedDetail.file,
                    encryptedDetail.title,
                    encryptedDetail.uuid
                )
            }
        )
    }

    fun decryptDetailFiles(
        encryptedDetail: EncryptedDetail,
        sdek: KeysetHandle
    ): Completable {
        val fileEncrypter = FileEncryptionService()
        return fileEncrypter.decrypt(encryptedDetail.file, sdek)
            .map { encryptedDetail.file = it }
            .ignoreElement()
    }
}

internal enum class DetailType {
    Audio,
    Text,
    Drawing,
    Photo,
    Video,
    ExternalVideo
}