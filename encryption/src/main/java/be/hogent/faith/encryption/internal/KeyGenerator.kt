package be.hogent.faith.encryption.internal

import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AeadKeyTemplates

class KeyGenerator {

    init {
        AeadConfig.register()
    }

    internal fun generateKeysetHandle(): KeysetHandle {
        val keyTemplate = AeadKeyTemplates.AES128_GCM
        return KeysetHandle.generateNew(keyTemplate)
    }
}