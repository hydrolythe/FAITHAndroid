package be.hogent.faith.encryption

import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.service.encryption.EncryptedAction
import be.hogent.faith.util.factory.ActionFactory
import org.junit.Test

class ActionEncryptionServiceTest {
    private val dek by lazy { KeyGenerator().generateKeysetHandle() }

    private val actionEncrypter =
        ActionEncryptionService()

    private val action = ActionFactory.makeAction()

    @Test
    fun `After decrypting an encrypted action its data is back to the original values`() {
        // Arrange
        lateinit var encryptedAction: EncryptedAction
        actionEncrypter.encrypt(action, dek)
            .doOnSuccess { encryptedAction = it }
            .test()
            .assertComplete()
            .dispose()

        // Act
        actionEncrypter.decrypt(encryptedAction, dek)
            .test()
            .assertComplete()
            .assertValue { decryptedAction ->
                action.description == decryptedAction.description &&
                        action.currentStatus == decryptedAction.currentStatus
            }
            .dispose()
    }
}