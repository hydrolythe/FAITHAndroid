package be.hogent.faith.encryption.internal

import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadFactory
import com.google.crypto.tink.subtle.Base64

/**
 * This DataEncrypter is used for the data encrypting part of the Envelope Encryption used in this
 * module. (@see https://cloud.google.com/kms/docs/envelope-encryption)
 * It uses a [dataEncryptionKey] as DEK.
 * Encrypting of the DEK is done by the [be.hogent.faith.encryption.encryptionService.KeyEncryptionService].
 *
 * Functions in this class return [Base64]-encoded Strings instead of byte-arrays
 * (as is more usual in encryption) because Firebase does not support storing byte-arrays.
 */
class DataEncrypter(
    dek: KeysetHandle
) {
    private val dataEncryptionKey = AeadFactory.getPrimitive(dek)

    /**
     * Returns a [Base64]-encoded, encrypted version of the given [plaintext]
     */
    internal fun encrypt(plaintext: String): String {
        val stringBytes = plaintext.toByteArray(Charsets.UTF_8)
        val encryptedBytes = dataEncryptionKey.encrypt(stringBytes, null)
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    /**
     * Returns the decrypted version of a String
     */
    internal fun decrypt(encryptedString: String): String {
        val decryptableBytes = Base64.decode(encryptedString, Base64.DEFAULT)
        val plaintextBytes = dataEncryptionKey.decrypt(decryptableBytes, null)
        return plaintextBytes.toString(Charsets.UTF_8)
    }
}
