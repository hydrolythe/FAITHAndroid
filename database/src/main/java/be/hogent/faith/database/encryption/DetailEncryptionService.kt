package be.hogent.faith.database.encryption

import be.hogent.faith.domain.models.detail.Detail

interface DetailEncryptionService {
    fun encrypt(detail: Detail): EncryptedDetailInterface
    fun decrypt(encryptedDetail: EncryptedDetailInterface): Detail
}
