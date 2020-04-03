package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.storage.local.ITemporaryFileStorageRepository
import be.hogent.faith.service.usecases.event.SaveEventDetailUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SaveEventTextDetailUseCaseTest {
    private lateinit var saveEventTextDetailUseCase: SaveEventDetailUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: ITemporaryFileStorageRepository = mockk(relaxed = true)

    private val event = Event()
    private val detail = mockk<TextDetail>()

    @Before
    fun setUp() {
        saveEventTextDetailUseCase =
            SaveEventDetailUseCase(
                repository,
                scheduler
            )
    }

    @Test
    fun saveTextUC_saveTextNormal_savedToStorage() {
        // Arrange
        every { repository.saveDetailFileWithContainer(event, detail) } returns Completable.complete()
        val params = SaveEventDetailUseCase.Params(detail, event)

        // Act
        saveEventTextDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { repository.saveDetailFileWithContainer(event, detail) }
    }

    @Test
    fun saveTextUC_saveTextNormal_addedToEvent() {
        // Arrange
        every { repository.saveDetailFileWithContainer(event, detail) } returns Completable.complete()
        val params = SaveEventDetailUseCase.Params(detail, event)

        // Act
        saveEventTextDetailUseCase.buildUseCaseObservable(params).test()

        // Assert
        val resultingDetail = event.details.first()
        assertEquals(detail, resultingDetail)
    }

    @Test
    fun saveTextUC_errorInRepo_notAddedToEvent() {
        // Arrange
        every { repository.saveDetailFileWithContainer(event, detail) } returns Completable.error(
            RuntimeException()
        )
        val params = SaveEventDetailUseCase.Params(detail, event)

        // Act
        saveEventTextDetailUseCase.buildUseCaseObservable(params).test()
            .assertError(java.lang.RuntimeException::class.java)

        // Assert
        Assert.assertTrue(event.details.isEmpty())
    }

    @Test
    fun saveTextUC_existingDetail_NOP() {
        // Arrange
        event.addDetail(detail)
        val params = SaveEventDetailUseCase.Params(detail, event)

        // Act
        saveEventTextDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        assertEquals(1, event.details.size)
        assertEquals(detail, event.details.first())
    }
}