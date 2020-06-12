package be.hogent.faith.database.detailcontainer

import be.hogent.faith.database.common.EncryptedDetailEntity
import be.hogent.faith.service.encryption.EncryptedString

data class EncryptedDetailsContainerEntity(
    val type: String = "",
    val details: List<EncryptedDetailEntity> = emptyList(),
    val encryptedDEK: EncryptedString = "",
    val encryptedStreamingDEK: EncryptedString = ""
)