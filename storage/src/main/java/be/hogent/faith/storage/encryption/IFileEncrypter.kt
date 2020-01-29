package be.hogent.faith.storage.encryption

import io.reactivex.Completable
import java.io.File

interface IFileEncrypter {
    /**
     * Replaces the contents of a file with an encrypted version
     */
    fun encrypt(file: File): Completable

    /**
     * Replaces the contents of an encrypted file with a decrypted version
     */
    fun decrypt(file: File): Completable
}
