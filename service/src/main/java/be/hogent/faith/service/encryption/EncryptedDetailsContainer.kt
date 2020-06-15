package be.hogent.faith.service.encryption

enum class ContainerType {
    CINEMA,
    BACKPACK,
    TREASURECHEST
}

class EncryptedDetailsContainer(
    val containerType: ContainerType,
    /**
     * An encrypted version of the Data Encryption Key, used for envelope encryption.
     * This DEK is used for encrypting small pieces of data directly.
     */
    val encryptedDEK: EncryptedString,
    /**
     * An encrypted version of the streaming Data Encryption Key, used for envelope encryption.
     * This DEK is used for encrypting entire files as a stream.
     */
    val encryptedStreamingDEK: EncryptedString
)
