package be.hogent.faith.storage.encryption

import java.io.File

class FileEncryptor(
    private val encrypter: Encrypter
) {
    fun encrypt(file: File) {
        val fileContents = file.readText()
        val encryptedContents = encrypter.encrypt(fileContents)!!
        file.writeText(encryptedContents)
    }

    fun decrypt(file: File) {
        val fileContents = file.readText()
        val decryptedContents = encrypter.decrypt(fileContents)!!
        file.writeText(decryptedContents)
    }
}