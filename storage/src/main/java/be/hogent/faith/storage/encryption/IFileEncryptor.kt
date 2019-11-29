package be.hogent.faith.storage.encryption

import java.io.File

interface IFileEncryptor {
    /**
     * Replaces the contents of a file with an encrypted version
     */
    fun encrypt(file: File)

    /**
     * Replaces the contents of an encrypted file with a decrypted version
     */
    fun decrypt(file: File)
}
