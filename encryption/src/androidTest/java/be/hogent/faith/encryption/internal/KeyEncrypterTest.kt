package be.hogent.faith.encryption.internal

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import be.hogent.faith.encryption.di.encryptionModule
import com.google.crypto.tink.KeysetHandle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import java.util.Objects

/**
 * Test for KeyEncrypter that uses the actual encryption
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class KeyEncrypterTest : KoinTest {

    private val keyGenerator = KeyGenerator()

    private lateinit var encryptionService: KeyEncryptionService

    private lateinit var keyEncrypter: KeyEncrypter

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(encryptionModule)
    }

    @Before
    fun setUp() {
        encryptionService = get()
        keyEncrypter = KeyEncrypter(encryptionService)
    }

    @Test
    @Ignore("Wait for keyEncrypter workaround to be deleted")
    fun transforms_a_given_keySetHandle_to_an_encrypted_String() {
        val keySetHandle = keyGenerator.generateKeysetHandle()

        keyEncrypter.encrypt(keySetHandle)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue(Objects::nonNull)
            .assertValue(String::isNotEmpty)
            .dispose()
    }

    @Test
    @Ignore("Wait for keyEncrypter workaround to be deleted")
    fun transforms_an_encrypted_keySetHandle_back_to_the_original() {
        val keySetHandle = keyGenerator.generateKeysetHandle()
        var encryptedKeySetHandle = ""
        var decryptedKeySetHandle: KeysetHandle? = null

        keyEncrypter.encrypt(keySetHandle)
            .doOnSuccess {
                encryptedKeySetHandle = it
            }
            .test()
            .assertComplete()
            .dispose()

        keyEncrypter.decrypt(encryptedKeySetHandle)
            .doOnSuccess { decryptedKeySetHandle = it }
            .test()
            .assertComplete()
            .assertNoErrors()
            .dispose()

        // Have to use keySetInfo because comparing the keysets themselves differ by some whitespace only.
        assertEquals(decryptedKeySetHandle!!.keysetInfo, keySetHandle.keysetInfo)
    }
}