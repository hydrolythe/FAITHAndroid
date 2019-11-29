package be.hogent.faith.storage.encryption

import java.io.File

class FileEncryptor(
    private val encrypter: Encrypter
) {
    /**
     * Replaces the contents of a file with an encrypted version
     */
    fun encrypt(file: File) {
        val fileContents = file.readText()
        val encryptedContents = encrypter.encrypt(fileContents)!!
        file.writeText(encryptedContents)
    }

    /**
     * Replaces the contents of an encrypted file with a decrypted version
     */
    fun decrypt(file: File) {
        val fileContents = file.readText()
        val decryptedContents = encrypter.decrypt(fileContents)!!
        file.writeText(decryptedContents)
    }
}