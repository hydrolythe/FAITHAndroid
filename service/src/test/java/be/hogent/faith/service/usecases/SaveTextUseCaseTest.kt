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

class SaveTextUseCaseTest {
    private lateinit var saveTextUseCase: SaveTextUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: StorageRepository = mockk(relaxed = true)

    private lateinit var event: Event
    private val text = "<font size='5'>Hello <b>World</b></font>"

    @Before
    fun setUp() {
        saveTextUseCase = SaveTextUseCase(repository, scheduler)
        event = EventFactory.makeEvent(nbrOfDetails = 0)
    }

    @Test
    fun saveTextUC_saveTextNormal_savedToStorage() {
        // Arrange
        every { repository.writeHTML(text, event, TEXT_FILENAME) } returns Single.just(mockk())

        // Act
        saveTextUseCase.buildUseCaseObservable(
            SaveTextUseCase.SaveTextParams(event, text)
        ).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { repository.writeHTML(any(), event, TEXT_FILENAME) }
    }

    @Test
    fun saveTextUC_saveTextNormal_addedToEvent() {
        // Arrange
        every { repository.writeHTML(text, event, TEXT_FILENAME) } returns Single.just(mockk())

        // Act
        saveTextUseCase.buildUseCaseObservable(
            SaveTextUseCase.SaveTextParams(event, text)
        ).test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        Assert.assertTrue(event.details.isNotEmpty())

        val resultingDetail = event.details.first()
        Assert.assertTrue(resultingDetail is TextDetail)
        Assert.assertEquals(TEXT_FILENAME, resultingDetail.name)
    }

    @Test
    fun saveTextUC_errorInRepo_notAddedToEvent() {
        // Arrange
        every { repository.writeHTML(text, event, TEXT_FILENAME) } returns Single.error(IOException())

        // Act
        saveTextUseCase.buildUseCaseObservable(
            SaveTextUseCase.SaveTextParams(event, text)
        ).test()
            .assertError(IOException::class.java)

        // Assert
        Assert.assertTrue(event.details.isEmpty())
    }
}