package be.hogent.faith.encryption

import be.hogent.faith.database.encryption.EncryptedDetail
import be.hogent.faith.encryption.internal.DataEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.util.contentEqual
import be.hogent.faith.util.factory.DetailFactory
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class DetailEncryptionServiceTest {
    private val dek by lazy { KeyGenerator().generateKeysetHandle() }
    private val sdek by lazy { KeyGenerator().generateStreamingKeysetHandle() }

    private val detailEncrypter = DetailEncryptionService(DataEncrypter(dek), FileEncrypter(sdek))

    private val detail = DetailFactory.makeRandomDetail()

    private val dataFile =
        File("src/test/java/be/hogent/faith/encryption/testResources/image.png")

    private val backupFile =
        File("src/test/java/be/hogent/faith/encryption/testResources/image - copy.png")

    @Before
    fun setUp() {
        detail.file = dataFile
    }

    @After
    fun tearDown() {
        backupFile.copyTo(dataFile, overwrite = true)
    }

    @Test
    fun `After encrypting a detail its data is no longer human readable`() {
        // Act
        detailEncrypter.encrypt(detail)
            .test()
            .assertValue { encryptedDetail ->
                encryptedDetail.type != detail.javaClass.canonicalName
            }
    }

    @Test
    fun `After encrypting a detail its associated file is no longer human readable`() {
        // Act
        detailEncrypter.encrypt(detail)
            .test()
            .assertValue { encryptedDetail ->
                encryptedDetail.file.contentEqual(backupFile).not()
            }
    }

    @Test
    fun `After decrypting an encrypted detail its data is back to the original values`() {
        lateinit var encryptedDetail: EncryptedDetail
        detailEncrypter.encrypt(detail)
            .doOnSuccess { encryptedDetail = it }
            .test()
            .assertComplete()

        // Act
        detailEncrypter.decrypt(encryptedDetail)
            .test()
            .assertComplete()
            .assertValue { decryptedDetail ->
                detail == decryptedDetail
            }
    }

    @Test
    fun `After decrypting a detail its associated file is back to the original`() {
        lateinit var encryptedDetail: EncryptedDetail
        detailEncrypter.encrypt(detail)
            .doOnSuccess { encryptedDetail = it }
            .test()

        // Act
        detailEncrypter.decrypt(encryptedDetail)
            .test()
            .assertValue { decryptedDetail ->
                decryptedDetail.file.contentEqual(backupFile)
            }
    }
}