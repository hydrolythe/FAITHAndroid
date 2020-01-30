package be.hogent.faith.encryption.internal

import be.hogent.faith.encryption.encryptionService.DummyKeyEncryptionService
import be.hogent.faith.encryption.encryptionService.KeyEncryptionService
import com.google.crypto.tink.KeysetHandle
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Objects

/**
 * Test for KeyEncrypter that uses the actual encryption
 */
class KeyEncrypterTest {

    private val keyGenerator = KeyGenerator()

    private val encryptionServcice: KeyEncryptionService = DummyKeyEncryptionService()

    private val keyEncrypter = KeyEncrypter(encryptionServcice)

    @Test
    fun `transforms a given keySetHandle to an encrypted String`() {
        val keySetHandle = keyGenerator.generateKeysetHandle()

        keyEncrypter.encrypt(keySetHandle)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue(Objects::nonNull)
            .assertValue(String::isNotEmpty)
    }

    @Test
    fun `transforms an encrypted keySetHandle back to the original`() {
        val keySetHandle = keyGenerator.generateKeysetHandle()
        var encryptedKeySethandle = ""
        var decryptedKeySetHandle: KeysetHandle? = null

        keyEncrypter.encrypt(keySetHandle)
            .doOnSuccess { encryptedKeySethandle = it }
            .test()
            .assertComplete()

        keyEncrypter.decrypt(encryptedKeySethandle)
            .doOnSuccess { decryptedKeySetHandle = it }
            .test()


        // Have to use keySetInfo because comparing the keysets themselves differ by some whitespace only.
        assertEquals(decryptedKeySetHandle!!.keysetInfo, keySetHandle.keysetInfo)
    }
}