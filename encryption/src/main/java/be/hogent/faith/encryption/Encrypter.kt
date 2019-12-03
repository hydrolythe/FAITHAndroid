package be.hogent.faith.encryption

class Encrypter {

    init {
//        TinkConfig.register()
    }

//    private val keySethandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM)
//    private val aead = keySethandle.getPrimitive(Aead::class.java)

    fun encrypt(string: String): String {
        return "enc$string"
//        return aead.encrypt(string.toByteArray(), null).toString()
    }

    fun decrypt(string: String): String {
        return string.substring(3)
//        return aead.decrypt(string.toByteArray(), null).toString()
    }
}