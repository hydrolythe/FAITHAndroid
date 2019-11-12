package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.storage.StorageRepository
import be.hogent.faith.util.factory.DetailFactory
import be.hogent.faith.util.factory.EventFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

class SaveEventTextDetailUseCaseTest {
    private lateinit var createTextDetailUseCase: CreateTextDetailUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: StorageRepository = mockk(relaxed = true)

    private lateinit var event: Event
    private val text = "<font size='5'>Hello <b>World</b></font>"

    @Before
    fun setUp() {
        createTextDetailUseCase = CreateTextDetailUseCase(repository, scheduler)
        event = EventFactory.makeEvent(nbrOfDetails = 0)
    }

    @Test
    fun saveTextUC_saveTextNormal_savedToStorage() {
        // Arrange
        every { repository.saveText(text, event) } returns Single.just(mockk())

        // Act
        createTextDetailUseCase.buildUseCaseObservable(
            CreateTextDetailUseCase.Params(event, text)
        ).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { repository.saveText(text, event) }
    }

    @Test
    fun saveTextUC_saveTextNormal_addedToEvent() {
        // Arrange
        every { repository.saveText(text, event) } returns Single.just(mockk())

        // Act
        createTextDetailUseCase.buildUseCaseObservable(
            CreateTextDetailUseCase.Params(event, text)
        ).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        Assert.assertTrue(event.details.isNotEmpty())

        val resultingDetail = event.details.first()
        Assert.assertTrue(resultingDetail is TextDetail)
    }

    @Test
    fun saveTextUC_errorInRepo_notAddedToEvent() {
        // Arrange
        every { repository.saveText(text, event) } returns Single.error(IOException())

        // Act
        createTextDetailUseCase.buildUseCaseObservable(
            CreateTextDetailUseCase.Params(event, text)
        ).test()
            .assertError(IOException::class.java)

        // Assert
        Assert.assertTrue(event.details.isEmpty())
    }

    @Test
    fun saveTextUC_overwriteDetail_overWritesInRepo() {
        // Arrange
        val existingDetail = DetailFactory.makeTextDetail()
        event.addDetail(existingDetail)

        every {
            repository.overwriteTextDetail(
                text,
                existingDetail
            )
        } returns Completable.complete()

        // Act
        createTextDetailUseCase.buildUseCaseObservable(
            CreateTextDetailUseCase.Params(event, text, existingDetail)
        ).test()
            .assertNoErrors()
            .assertComplete()

        verify { repository.overwriteTextDetail(text, existingDetail) }
    }

    @Test
    fun saveTextUC_overwriteDetail_detailFileUnchanged() {
        // Arrange
        val existingDetail = DetailFactory.makeTextDetail()
        event.addDetail(existingDetail)

        every {
            repository.overwriteTextDetail(
                text,
                existingDetail
            )
        } returns Completable.complete()

        // Act
        createTextDetailUseCase.buildUseCaseObservable(
            CreateTextDetailUseCase.Params(event, text, existingDetail)
        ).test()

        assertEquals(existingDetail, event.details.first())
    }
}