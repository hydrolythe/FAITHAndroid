package be.hogent.faith.encryption

import org.junit.After
import java.io.File

open class TestWithFiles {
    @After
    fun cleanUpEncryptionFiles() {
        val testResourceFiles =
            File("src/test/java/be/hogent/faith/encryption/testResources").listFiles { _, fileName ->
                fileName.contains("encrypted") || fileName.contains("decrypted")
            }
        testResourceFiles?.forEach { it.delete() }

        val randomFiles =
            File("./").listFiles { _, fileName ->
                fileName.contains("encrypted") || fileName.contains("decrypted")
            }
        randomFiles?.forEach { it.delete() }
    }
}