package be.hogent.faith.encryption

import be.hogent.faith.storage.encryption.IFileEncryptor
import java.io.File

class FileEncryptor(
    private val encrypter: Encrypter
) : IFileEncryptor {
    /**
     * Replaces the contents of a file with an encrypted version
     */
    override fun encrypt(file: File) {
        val fileContents = file.readText()
        val encryptedContents = encrypter.encrypt(fileContents)!!
        file.writeText(encryptedContents)
    }

    /**
     * Replaces the contents of an encrypted file with a decrypted version
     */
    override fun decrypt(file: File) {
        val fileContents = file.readText()
        val decryptedContents = encrypter.decrypt(fileContents)!!
        file.writeText(decryptedContents)
    }
}
