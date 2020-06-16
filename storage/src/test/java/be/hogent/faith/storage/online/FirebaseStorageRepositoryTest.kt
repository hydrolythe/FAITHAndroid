package be.hogent.faith.storage.online

import android.content.Context
import android.net.Uri
import be.hogent.faith.service.encryption.EncryptedDetail
import be.hogent.faith.service.encryption.EncryptedEvent
import be.hogent.faith.storage.StoragePathProvider
import be.hogent.faith.util.factory.DataFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import io.reactivex.rxjava3.core.Single
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.UUID

private const val userUID = "uid"

class FirebaseStorageRepositoryTest {

    private val filesDir = File("local")
    private val context = mockk<Context>()
    private val fbAuth = mockk<FirebaseAuth>()

    private val storagePathProvider = StoragePathProvider(context, fbAuth)

    private val firebaseStorage = mockk<FirebaseStorage>()
    private val rxFirebaseStorage = mockk<IRxFireBaseStorage>()
    private lateinit var onlineFileStorageRepository:
            FirebaseStorageRepository

    private val detailFile = File("detail")
    private val encryptedDetail = EncryptedDetail(
        file = detailFile,
        title = DataFactory.randomString(),
        uuid = UUID.randomUUID(),
        type = "encrypted type",
        // Empty string to emulate a non-YoutubeVideoDetail
        youtubeVideoID = "",
        thumbnail = DataFactory.randomString(),
        dateTime = DataFactory.randomString()
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
        every { firebaseStorage.reference } returns mockk(relaxed = true)
        onlineFileStorageRepository =
            FirebaseStorageRepository(storagePathProvider, firebaseStorage, rxFirebaseStorage)

        every { context.filesDir } returns filesDir
        every { fbAuth.currentUser } returns mockk {
            every { uid } returns userUID
        }

        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockk()
    }

    @After
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `saveEvent saves the emotionAvatars file to online storage`() {
        // Arrange
        every { rxFirebaseStorage.putFile(any(), any()) } returns Single.just(mockk())
        // Act
        onlineFileStorageRepository.saveEventFiles(encryptedEvent)
            .test()
            .assertNoErrors()
            .assertComplete()
            .dispose()

        // Would be better to also check the reference to where it is put but I don't know
        // how to do that.
        // As such there's no difference between the test for storing the avatar and the details
        verify { rxFirebaseStorage.putFile(any(), any()) }
    }

    @Test
    fun `saveEvent saves all details' files to online storage`() {
        // Arrange
        every { rxFirebaseStorage.putFile(any(), any()) } returns Single.just(mockk())
        // Act
        onlineFileStorageRepository.saveEventFiles(encryptedEvent)
            .test()
            .assertNoErrors()
            .assertComplete()
            .dispose()

        // Would be better to also check the reference to where it is put but I don't know
        // how to do that.
        // As such there's no difference between the test for storing the avatar and the details
        verify(exactly = 2) {
            rxFirebaseStorage.putFile(any(), any())
        }
    }

    // TODO: find a way to test the avatar and the details separately
    @Test
    fun `saveEvent emits an error when saving the emotionAvatar fails`() {
        // Arrange
        every { rxFirebaseStorage.putFile(any(), any()) } returns Single.error(RuntimeException())
        // Act
        onlineFileStorageRepository.saveEventFiles(encryptedEvent)
            .test()
            .assertError(RuntimeException::class.java)
            .dispose()

        // Would be better to also check the reference to where it is put but I don't know
        // how to do that.
        // As such there's no difference between the test for storing the avatar and the details
        verify { rxFirebaseStorage.putFile(any(), any()) }
    }

    @Test
    fun `saveEvent emits an error when saving a detail fails`() {
        // Arrange
        every { rxFirebaseStorage.putFile(any(), any()) } returns Single.error(RuntimeException())
        // Act
        onlineFileStorageRepository.saveEventFiles(encryptedEvent)
            .test()
            .assertError(RuntimeException::class.java)
            .dispose()

        // Would be better to also check the reference to where it is put but I don't know
        // how to do that.
        // As such there's no difference between the test for storing the avatar and the details
        verify { rxFirebaseStorage.putFile(any(), any()) }
    }
}