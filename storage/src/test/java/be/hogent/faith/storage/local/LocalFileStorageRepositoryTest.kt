package be.hogent.faith.storage.local

import android.content.Context
import be.hogent.faith.service.encryption.EncryptedDetail
import be.hogent.faith.service.encryption.EncryptedEvent
import be.hogent.faith.storage.StoragePathProvider
import be.hogent.faith.util.factory.DataFactory
import com.google.firebase.auth.FirebaseAuth
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertEquals
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
        LocalFileStorageRepository(storagePathProvider)

    private val detailFile = File("detail")
    private val encryptedDetail = EncryptedDetail(
        file = detailFile,
        title = DataFactory.randomString(),
        uuid = UUID.randomUUID(),
        type = "encrypted type",
        // Empty string to emulate a non-YoutubeVideoDetail
        youtubeVideoID = "",
        dateTime = DataFactory.randomString(),
        thumbnail = DataFactory.randomString()
    )

    private val emotionAvatarFile = File("emotionAvatar")
    private val encryptedEvent = EncryptedEvent(
        dateTime = "encrypted DateTime",
        uuid = UUID.randomUUID(),
        title = "encrypted title",
        notes = "encrypted notes",
        emotionAvatar = emotionAvatarFile,
        emotionAvatarThumbnail = "encrypted thumbnail",
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
        localFileStorageRepository.saveEventFiles(encryptedEvent)
            .test()
            .assertComplete()
            .assertNoErrors()
            .dispose()

        // Assert
        val detailLocation =
            File(filesDir, "users/$userUID/events/${encryptedEvent.uuid}/${encryptedDetail.uuid}")
        assertEquals(detailLocation, encryptedDetail.file)
        assertTrue(detailLocation.exists())
    }

    /**
     * The correct path for the emotion avatar follows the pattern: users/[userUid]/events/[eventUUID]/emotionAvatar
     */
    @Test
    fun `saveEvent stores the emotion Avatar to local Storage`() {
        // Act
        localFileStorageRepository.saveEventFiles(encryptedEvent)
            .test()
            .assertComplete()
            .assertNoErrors()
            .dispose()

        // Assert
        val avatarLocation =
            File(filesDir, "users/$userUID/events/${encryptedEvent.uuid}/emotionAvatar")
        assertTrue(avatarLocation.exists())
    }

    @Test
    fun `saveEvent updates the event's emotion avatar to the location where it has been saved`() {
        // Act
        localFileStorageRepository.saveEventFiles(encryptedEvent)
            .test()
            .assertComplete()
            .assertNoErrors()
            .dispose()

        // Assert
        val avatarLocation =
            File(filesDir, "users/$userUID/events/${encryptedEvent.uuid}/emotionAvatar")
        assertEquals(avatarLocation, encryptedEvent.emotionAvatar)
    }

    @Test
    fun `saveEvent updates the event's detail's locations to the location where they have been saved`() {
        // Act
        localFileStorageRepository.saveEventFiles(encryptedEvent)
            .test()
            .assertComplete()
            .assertNoErrors()
            .dispose()

        // Assert
        assertTrue(encryptedEvent.details.all {
            it.file == File(filesDir, "users/$userUID/events/${encryptedEvent.uuid}/${it.uuid}")
        })
    }
}