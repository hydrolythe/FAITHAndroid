package be.hogent.faith.database.common

import be.hogent.faith.service.usecases.encryption.EncryptedString

data class EncryptedDetailEntity(
    var file: EncryptedString = "",
    var title: EncryptedString = "",
    // TODO: check why these all need default args
    val uuid: EncryptedString = "",
    val type: EncryptedString = ""
)
