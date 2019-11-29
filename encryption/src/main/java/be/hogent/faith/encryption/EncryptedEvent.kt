package be.hogent.faith.encryption

import be.hogent.faith.storage.encryption.IEncryptedEvent

class EncryptedEvent(
    override val dateTime: String,
    override val title: String?,
    override val emotionAvatar: String?,
    override val notes: String?,
    override val uuid: String
) : IEncryptedEvent
