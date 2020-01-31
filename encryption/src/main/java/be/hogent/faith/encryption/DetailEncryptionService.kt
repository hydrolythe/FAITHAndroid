package be.hogent.faith.encryption

import be.hogent.faith.database.encryption.DetailEncryptionService
import be.hogent.faith.database.encryption.EncryptedDetail
import be.hogent.faith.database.models.EncryptedDetailEntity
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.encryption.internal.DataEncrypter
import io.reactivex.Completable
import io.reactivex.Single

class DetailEncryptionService(
    private val dataEncrypter: DataEncrypter,
    private val fileEncrypter: FileEncrypter
) : DetailEncryptionService {

    /**
     * Encrypts a [EncryptedDetailEntity].
     * The file attribute is not encrypted because it contains no sensitive information and
     * it makes it easier to save it permanently later on.
     */
    override fun encrypt(detail: Detail): Single<EncryptedDetail> {
        return encryptDetailFiles(detail).andThen(encryptDetailData(detail))
    }

    private fun encryptDetailFiles(detail: Detail): Completable {
        return fileEncrypter.encrypt(detail.file)
    }

    private fun encryptDetailData(detail: Detail): Single<EncryptedDetail> {
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

    override fun decrypt(encryptedDetail: EncryptedDetail): Single<Detail> {
        return decryptDetailFiles(encryptedDetail).andThen(decryptDetailData(encryptedDetail))
    }

    private fun decryptDetailData(encryptedDetail: EncryptedDetail): Single<Detail> {
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

    private fun decryptDetailFiles(encryptedDetail: EncryptedDetail): Completable {
        return fileEncrypter.decrypt(encryptedDetail.file)
    }
}

internal enum class DetailType {
    Audio,
    Text,
    Drawing,
    Photo
}
