package be.hogent.faith.encryption

import be.hogent.faith.database.encryption.EncryptedDetailEntity
import be.hogent.faith.database.encryption.IDetailEntityEncrypter
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.DetailType

class DetailEntityEncrypter(
    private val encrypter: Encrypter
) : IDetailEntityEncrypter {

    override fun encrypt(detailEntity: DetailEntity): EncryptedDetailEntity {
        with(detailEntity) {
            return EncryptedDetailEntity(
                file = encrypter.encrypt(file),
                uuid = encrypter.encrypt(uuid),
                type = encrypter.encrypt(type.toString())
            )
        }
    }

    override fun decrypt(encryptedDetailEntity: EncryptedDetailEntity): DetailEntity {
        with(encryptedDetailEntity) {
            return DetailEntity(
                file = encrypter.decrypt(file),
                uuid = encrypter.decrypt(uuid),
                type = DetailType.valueOf(encrypter.decrypt(type))
            )
        }
    }
}
