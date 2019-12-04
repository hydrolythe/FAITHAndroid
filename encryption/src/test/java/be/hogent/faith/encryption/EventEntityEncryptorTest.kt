package be.hogent.faith.encryption

import be.hogent.faith.database.converters.LocalDateTimeConverter
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.util.factory.DataFactory
import org.junit.Assert.assertEquals
import org.junit.Test

class EventEntityEncryptorTest {
    private val encrypter = Encrypter()
    private val detailEntityEncrypter = DetailEntityEncrypter(encrypter)
    private val eventEntityEncryptor = EventEntityEncryptor(encrypter, detailEntityEncrypter)

    //    private val eventEntity = EntityFactory.makeEventEntityWithDetails(nbrOfDetails = 3)
    private val eventEntity = EventEntity(
        LocalDateTimeConverter().toString(DataFactory.randomDateTime()),
        "test",
        "testpath",
        "string",
        "uuuid",
        emptyList()
    )

    @Test
    fun eventEntityEncryptor_canEncryptWithoutErrors() {
        eventEntityEncryptor.encrypt(eventEntity)
    }

    @Test
    fun eventEntityEncryptor_canDecryptEncryptedEntity() {
        // Arrange
        val encryptedEntity = eventEntityEncryptor.encrypt(eventEntity)

        // Act
        val decryptedEntity = eventEntityEncryptor.decrypt(encryptedEntity)

        // Assert
        assertEquals(eventEntity, decryptedEntity)
    }

}