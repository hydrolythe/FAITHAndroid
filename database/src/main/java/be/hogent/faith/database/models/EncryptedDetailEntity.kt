package be.hogent.faith.database.models

import be.hogent.faith.database.encryption.EncryptedString

data class EncryptedDetailEntity(
    var file: EncryptedString = "",
    // TODO: check why these all need default args
    val uuid: EncryptedString = "",
    val type: EncryptedString = ""
)
