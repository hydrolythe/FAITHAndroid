package be.hogent.faith.database.encryption

import be.hogent.faith.domain.models.detail.Detail
import io.reactivex.Single

interface DetailEncryptionService {
    fun encrypt(detail: Detail): Single<EncryptedDetail>
    fun decrypt(encryptedDetail: EncryptedDetail): Single<Detail>
}
