package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.usecases.event.SaveEventTextDetailUseCase
import be.hogent.faith.storage.localStorage.ITemporaryStorageRepository
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
    private lateinit var saveEventTextDetailUseCase: SaveEventTextDetailUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: ITemporaryStorageRepository = mockk(relaxed = true)

    private val event = Event()
    private val detail = mockk<TextDetail>()

    @Before
    fun setUp() {
        saveEventTextDetailUseCase =
            SaveEventTextDetailUseCase(
                repository,
                scheduler
            )
    }

    @Test
    fun saveTextUC_saveTextNormal_savedToStorage() {
        // Arrange
        every { repository.storeDetailWithEvent(detail, event) } returns Completable.complete()
        val params = SaveEventTextDetailUseCase.Params(detail, event)

        // Act
        saveEventTextDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { repository.storeDetailWithEvent(detail, event) }
    }

    @Test
    fun saveTextUC_saveTextNormal_addedToEvent() {
        // Arrange
        every { repository.storeDetailWithEvent(detail, event) } returns Completable.complete()
        val params = SaveEventTextDetailUseCase.Params(detail, event)

        // Act
        saveEventTextDetailUseCase.buildUseCaseObservable(params).test()

        // Assert
        val resultingDetail = event.details.first()
        assertEquals(detail, resultingDetail)
    }

    @Test
    fun saveTextUC_errorInRepo_notAddedToEvent() {
        // Arrange
        every { repository.storeDetailWithEvent(detail, event) } returns Completable.error(
            RuntimeException()
        )
        val params = SaveEventTextDetailUseCase.Params(detail, event)

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
        val params = SaveEventTextDetailUseCase.Params(detail, event)

        // Act
        saveEventTextDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        assertEquals(1, event.details.size)
        assertEquals(detail, event.details.first())
    }
}