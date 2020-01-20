package be.hogent.faith.database.encryption

import be.hogent.faith.database.models.DetailEntity

// TODO: users of the storage module should define this interface, not storage module itself
interface IDetailEntityEncrypter {
    fun encrypt(detailEntity: DetailEntity): EncryptedDetailEntity
    fun decrypt(encryptedDetailEntity: EncryptedDetailEntity): DetailEntity
}

class EncryptedDetailEntity(
    val file: String = "",
    val uuid: String = "",
    val type: String = ""
)
