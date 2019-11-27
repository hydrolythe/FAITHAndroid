package be.hogent.faith.storage.encryption

import be.hogent.faith.util.factory.EventFactory
import org.junit.Assert.assertEquals
import org.junit.Test

class EncryptionMapperTest {

    private val event = EventFactory.makeEvent(3)
    private val mapper = EventEncryptor(Encrypter())

    @Test
    fun encryptionMapper_encryptDecryptIdempotent() {
        assertEquals(event, mapper.decrypt(mapper.encrypt(event)))
    }
}