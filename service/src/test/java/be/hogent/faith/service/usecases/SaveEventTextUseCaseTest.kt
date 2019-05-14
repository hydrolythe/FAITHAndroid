package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.storage.StorageRepository
import be.hogent.faith.util.factory.EventFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Scheduler
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException

class SaveEventTextUseCaseTest {
    private lateinit var saveEventTextUseCase: SaveEventTextUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: StorageRepository = mockk(relaxed = true)

    private lateinit var event: Event
    private val text = "<font size='5'>Hello <b>World</b></font>"

    @Before
    fun setUp() {
        saveEventTextUseCase = SaveEventTextUseCase(repository, scheduler)
        event = EventFactory.makeEvent(nbrOfDetails = 0)
    }

    @Test
    fun saveTextUC_saveTextNormal_savedToStorage() {
        // Arrange
        every { repository.saveText(text, event) } returns Single.just(mockk())

        // Act
        saveEventTextUseCase.buildUseCaseObservable(
            SaveEventTextUseCase.SaveTextParams(event, text)
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
        saveEventTextUseCase.buildUseCaseObservable(
            SaveEventTextUseCase.SaveTextParams(event, text)
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
        saveEventTextUseCase.buildUseCaseObservable(
            SaveEventTextUseCase.SaveTextParams(event, text)
        ).test()
            .assertError(IOException::class.java)

        // Assert
        Assert.assertTrue(event.details.isEmpty())
    }
}