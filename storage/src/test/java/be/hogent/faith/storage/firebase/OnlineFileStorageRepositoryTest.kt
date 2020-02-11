package be.hogent.faith.storage.firebase

import android.content.Context
import android.net.Uri
import be.hogent.faith.database.encryption.EncryptedDetail
import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.storage.StoragePathProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.UUID

private const val userUID = "uid"

class OnlineFileStorageRepositoryTest {

    private val filesDir = File("local")
    private val context = mockk<Context>()
    private val fbAuth = mockk<FirebaseAuth>()

    private val storagePathProvider = StoragePathProvider(context, fbAuth)

    private val firebaseStorage = mockk<FirebaseStorage>()
    private val rxFirebaseStorage = mockk<IRxFireBaseStorage>()
    private lateinit var onlineFileStorageRepository:
            OnlineFileStorageRepository

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
        every { firebaseStorage.getReference() } returns mockk(relaxed = true)
        onlineFileStorageRepository =
            OnlineFileStorageRepository(storagePathProvider, firebaseStorage, rxFirebaseStorage)

        every { context.filesDir } returns filesDir
        every { fbAuth.currentUser } returns mockk {
            every { uid } returns userUID
        }

        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockk()
    }

    @Test
    fun `saveEvent saves the emotionAvatars file to online storage`() {
        // Arrange
        every { rxFirebaseStorage.putFile(any(), any()) } returns Single.just(mockk())
        // Act
        onlineFileStorageRepository.saveEvent(encryptedEvent)
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
    fun `saveEvent saves all detail's files to online storage`() {
        // Arrange
        every { rxFirebaseStorage.putFile(any(), any()) } returns Single.just(mockk())
        // Act
        onlineFileStorageRepository.saveEvent(encryptedEvent)
            .test()
            .assertNoErrors()
            .assertComplete()
            .dispose()

        // Would be better to also check the reference to where it is put but I don't know
        // how to do that.
        // As such there's no difference between the test for storing the avatar and the details
        verify { rxFirebaseStorage.putFile(any(), any()) }
    }

    // TODO: find a way to test the avatar and the details separately
    @Test
    fun `saveEvent emits an error when saving the emotionAvatar fails`() {
        // Arrange
        every { rxFirebaseStorage.putFile(any(), any()) } returns Single.error(RuntimeException())
        // Act
        onlineFileStorageRepository.saveEvent(encryptedEvent)
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
        onlineFileStorageRepository.saveEvent(encryptedEvent)
            .test()
            .assertError(RuntimeException::class.java)
            .dispose()

        // Would be better to also check the reference to where it is put but I don't know
        // how to do that.
        // As such there's no difference between the test for storing the avatar and the details
        verify { rxFirebaseStorage.putFile(any(), any()) }
    }
}