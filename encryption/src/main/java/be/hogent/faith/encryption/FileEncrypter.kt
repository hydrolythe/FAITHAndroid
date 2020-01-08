package be.hogent.faith.encryption

import be.hogent.faith.encryption.internal.DataEncrypter
import be.hogent.faith.storage.encryption.IFileEncrypter
import com.google.crypto.tink.Aead
import java.io.File

class FileEncrypter(
    dataEncryptionKey: Aead
) : IFileEncrypter {

    private val dataEncrypter =
        DataEncrypter(dataEncryptionKey)

    /**
     * Replaces the contents of a file with an encrypted version
     */
    override fun encrypt(file: File) {
        val fileContents = file.readText()
        val encryptedContents = dataEncrypter.encrypt(fileContents)
        file.writeText(encryptedContents)
    }

    /**
     * Replaces the contents of an encrypted file with a decrypted version
     */
    override fun decrypt(file: File) {
        val fileContents = file.readText()
        val decryptedContents = dataEncrypter.decrypt(fileContents)
        file.writeText(decryptedContents)
    }
}
