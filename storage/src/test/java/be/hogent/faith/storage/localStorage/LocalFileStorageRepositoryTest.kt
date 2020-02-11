package be.hogent.faith.storage.localStorage

import android.content.Context
import be.hogent.faith.database.encryption.EncryptedDetail
import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.storage.StoragePathProvider
import com.google.firebase.auth.FirebaseAuth
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.util.UUID

private const val userUID = "uid"

class LocalFileStorageRepositoryTest {

    private val context = mockk<Context>()
    private val fbAuth = mockk<FirebaseAuth>()

    private val storagePathProvider = StoragePathProvider(context, fbAuth)
    private val filesDir = File("local")

    private val localFileStorageRepository =
        LocalFileStorageRepository(storagePathProvider, context)

    private val detailFile = File("detail")
    private val encryptedDetail = EncryptedDetail(
        file = detailFile,
        uuid = UUID.randomUUID(),
        type = "encrypted type"
    )

    val emotionAvatarFile = File("emotionAvatar")
    private val encryptedEvent = EncryptedEvent(
        dateTime = "encrypted DateTime",
        uuid = UUID.randomUUID(),
        title = "encrypted title",
        notes = "encrypted notes",
        emotionAvatar = emotionAvatarFile,
        details = listOf(encryptedDetail),
        encryptedDEK = "encrypted DEK",
        encryptedStreamingDEK = "encrypted SDEK"
    )

    @Before
    fun setUp() {
        Files.createFile(emotionAvatarFile.toPath())
        Files.createFile(detailFile.toPath())
        filesDir.mkdir()
        every { context.filesDir } returns filesDir
        every { fbAuth.currentUser } returns mockk {
            every { uid } returns userUID
        }
    }

    @After
    fun tearDown() {
        filesDir.deleteRecursively()
        detailFile.delete()
        emotionAvatarFile.delete()
    }

    /**
     * The correct path follows the pattern: users/[userUid]/events/[eventUUID]/[detailUUID]
     */
    @Test
    fun `saveEvent stores the details to local Storage using the correct path`() {
        // Act
        localFileStorageRepository.saveEvent(encryptedEvent)
            .test()
            .assertComplete()
            .assertNoErrors()
            .dispose()

        // Assert
        val detailLocation =
            File(filesDir, "users/$userUID/events/${encryptedEvent.uuid}/${encryptedDetail.uuid}")
        assertTrue(detailLocation.exists())
    }

    /**
     * The correct path for the emotion avatar follows the pattern: users/[userUid]/events/[eventUUID]/emotionAvatar
     */
    @Test
    fun `saveEvent stores the emotion Avatar to local Storage`() {
        // Act
        localFileStorageRepository.saveEvent(encryptedEvent)
            .test()
            .assertComplete()
            .assertNoErrors()
            .dispose()

        // Assert
        val avatarLocation =
            File(filesDir, "users/$userUID/events/${encryptedEvent.uuid}/emotionAvatar")
        assertTrue(avatarLocation.exists())
    }
}