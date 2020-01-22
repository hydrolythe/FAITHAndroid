package be.hogent.faith.encryption

import be.hogent.faith.database.encryption.EncryptedDetail
import be.hogent.faith.database.encryption.EncryptedDetailEntity
import be.hogent.faith.database.encryption.DetailEncryptionService
import be.hogent.faith.database.models.EncryptedDetailEntity
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.encryption.internal.DataEncrypter

class DetailEncryptionService(
    private val dataEncrypter: DataEncrypter
) : DetailEncryptionService {

    /**
     * Encrypts a [EncryptedDetailEntity].
     * The file attribute is not encrypted because it contains no sensitive information and
     * it makes it easier to save it permanently later on.
     */
    override fun encrypt(detail: Detail): EncryptedDetail {
        with(detail) {
            return EncryptedDetailEntity(
                file = file,
                uuid = dataEncrypter.encrypt(uuid),
                type = dataEncrypter.encrypt(type.toString())
            )
        }
    }

    override fun decrypt(encryptedDetail: EncryptedDetail): Detail {
        with(encryptedDetail) {
            return EncryptedDetailEntity(
                file = file,
                uuid = dataEncrypter.decrypt(uuid),
                type = DetailType.valueOf(dataEncrypter.decrypt(type))
            )
        }
    }
}
