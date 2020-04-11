package be.hogent.faith.database.common

import be.hogent.faith.service.encryption.EncryptedString

data class EncryptedDetailEntity(
    var file: EncryptedString = "",
    var title: EncryptedString = "",
    val uuid: EncryptedString = "",
    val type: EncryptedString = ""
)
