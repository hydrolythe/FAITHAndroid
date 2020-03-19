package be.hogent.faith.encryption

import be.hogent.faith.database.encryption.EncryptedDetail
import be.hogent.faith.database.models.EncryptedDetailEntity
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.encryption.internal.DataEncrypter
import com.google.crypto.tink.KeysetHandle
import io.reactivex.Completable
import io.reactivex.Single

class DetailEncryptionService {

    /**
     * Encrypts a [EncryptedDetailEntity].
     * The file attribute is not encrypted because it contains no sensitive information and
     * it makes it easier to save it permanently later on.
     */
    fun encrypt(detail: Detail, dek: KeysetHandle, sdek: KeysetHandle): Single<EncryptedDetail> {
        return encryptDetailFiles(detail, sdek)
            .andThen(encryptDetailData(detail, dek))
    }

    private fun encryptDetailFiles(detail: Detail, sdek: KeysetHandle): Completable {
        val fileEncrypter = FileEncrypter(sdek)
        return fileEncrypter.encrypt(detail.file)
    }

    private fun encryptDetailData(detail: Detail, dek: KeysetHandle): Single<EncryptedDetail> {
        val dataEncrypter = DataEncrypter(dek)
        return Single.just(
            EncryptedDetail(
                file = detail.file,
                uuid = detail.uuid,
                type = dataEncrypter.encrypt(
                    when (detail) {
                        is AudioDetail -> DetailType.Audio
                        is TextDetail -> DetailType.Text
                        is DrawingDetail -> DetailType.Drawing
                        is PhotoDetail -> DetailType.Photo
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
                DetailType.Audio -> AudioDetail(encryptedDetail.file, encryptedDetail.uuid)
                DetailType.Text -> TextDetail(encryptedDetail.file, encryptedDetail.uuid)
                DetailType.Drawing -> DrawingDetail(encryptedDetail.file, encryptedDetail.uuid)
                DetailType.Photo -> PhotoDetail(encryptedDetail.file, encryptedDetail.uuid)
            }
        )
    }

    fun decryptDetailFiles(
        encryptedDetail: EncryptedDetail,
        sdek: KeysetHandle
    ): Completable {
        val fileEncrypter = FileEncrypter(sdek)
        return fileEncrypter.decrypt(encryptedDetail.file)
    }
}

internal enum class DetailType {
    Audio,
    Text,
    Drawing,
    Photo
}
