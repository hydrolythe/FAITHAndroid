import be.hogent.faith.domain.models.Event
import be.hogent.faith.encryption.internal.KeyEncrypter

class DEKProvider(
    private val keyEncrypter: KeyEncrypter
) {
    fun getDEKFrom(event: Event) {
        return keyEncrypter.decrypt()
    }
}