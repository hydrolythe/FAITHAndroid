package be.hogent.faith.encryption

import be.hogent.faith.database.encryption.EncryptedDetailEntity
import be.hogent.faith.database.encryption.IDetailEntityEncrypter
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.DetailType
import be.hogent.faith.encryption.internal.DataEncrypter

class DetailEntityEncrypter(
    private val dataEncrypter: DataEncrypter
) : IDetailEntityEncrypter {

    /**
     * Encrypts a [DetailEntity].
     * The file attribute is not encrypted because it contains no sensitive information and
     * it makes it easier to save it permanently later on.
     */
    override fun encrypt(detailEntity: DetailEntity): EncryptedDetailEntity {
        with(detailEntity) {
            return EncryptedDetailEntity(
                file = file,
                uuid = dataEncrypter.encrypt(uuid),
                type = dataEncrypter.encrypt(type.toString())
            )
        }
    }

    override fun decrypt(encryptedDetailEntity: EncryptedDetailEntity): DetailEntity {
        with(encryptedDetailEntity) {
            return DetailEntity(
                file = file,
                uuid = dataEncrypter.decrypt(uuid),
                type = DetailType.valueOf(dataEncrypter.decrypt(type))
            )
        }
    }
}
