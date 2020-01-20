package be.hogent.faith.encryption.internal

import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.config.TinkConfig
import com.google.crypto.tink.streamingaead.StreamingAeadKeyTemplates

class KeyGenerator {

    init {
        TinkConfig.register()

    }

    internal fun generateKeysetHandle(): KeysetHandle {
        val keyTemplate = AeadKeyTemplates.AES128_GCM
        return KeysetHandle.generateNew(keyTemplate)
    }

    internal fun generateStreamingKeysetHandle(): KeysetHandle {
        val keyTemplate = StreamingAeadKeyTemplates.AES128_GCM_HKDF_4KB
        return KeysetHandle.generateNew(keyTemplate)
    }
}