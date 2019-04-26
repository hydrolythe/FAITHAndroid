package be.hogent.faith.faith.enterEventDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.SaveEmotionAvatarUseCase
import be.hogent.faith.service.usecases.SaveEventUseCase
import be.hogent.faith.util.factory.EventFactory
import io.mockk.Called
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Completable
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.io.IOException

class EventDetailsViewModelTest {
    private lateinit var viewModel: EventDetailsViewModel
    private var saveEventUseCase = mockk<SaveEventUseCase>()

    private val eventTitle = "Event title"
    private val eventNotes = "Event notes\nOther Line in notes."
    private val eventDateTime = LocalDate.of(2019, 2, 20).atStartOfDay()
    private val saveEmotionAvatarUseCase = mockk<SaveEmotionAvatarUseCase>()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = EventDetailsViewModel(
            saveEventUseCase,
            saveEmotionAvatarUseCase,
            user = mockk(relaxed = true)
        )

        // We have to add an observer so event changes when title/notes/date are given a new value
        viewModel.event.observeForever(mockk(relaxed = true))

        viewModel.eventTitle.value = eventTitle
        viewModel.eventNotes.value = eventNotes
        viewModel.eventDate.value = eventDateTime
    }

    @Test
    fun eventViewModel_updateEvent_callsListeners() {
        // Arrange
        val observer = mockk<Observer<Event>>(relaxed = true)
        viewModel.event.observeForever(observer)

        // Act
        viewModel.updateEvent()

        // Assert
        verify { observer.onChanged(any()) }
    }

    @Test
    fun eventViewModel_eventTitleOnchange_changesEvent() {
        // Arrange
        val observer = mockk<Observer<Event>>(relaxed = true)
        viewModel.event.observeForever(observer)
        val newEventTitle = "New Title"

        // Act
        viewModel.eventTitle.postValue(newEventTitle)

        // Assert
        assertEquals(newEventTitle, viewModel.event.value!!.title)
    }

    @Test
    fun eventViewModel_eventNotesOnchange_changesEvent() {
        // Arrange
        val observer = mockk<Observer<Event>>(relaxed = true)
        viewModel.event.observeForever(observer)
        val newEventNotes = "New event notes"

        // Act
        viewModel.eventNotes.postValue(newEventNotes)

        // Assert
        assertEquals(newEventNotes, viewModel.event.value!!.notes)
    }

    @Test
    fun eventViewModel_eventDateOnchange_changesEvent() {
        // Arrange
        val observer = mockk<Observer<Event>>(relaxed = true)
        viewModel.event.observeForever(observer)
        val newEventDate = LocalDate.of(2018, 2, 10).atStartOfDay()

        // Act
        viewModel.eventDate.postValue(newEventDate)

        // Assert
        assertEquals(newEventDate, viewModel.event.value!!.dateTime)
    }

    @Test
    fun eventViewModel_saveButtonClicked_nullTitle_noUseCaseCall_errorMessage() {
        // Arrange
        viewModel.eventTitle.value = null
        val errorMessageObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.inputErrorMessageID.observeForever(errorMessageObserver)

        // Act
        viewModel.onSaveButtonClicked()

        // Assert
        verify { saveEventUseCase wasNot called }
        verify { errorMessageObserver.onChanged(any()) }
    }

    @Test
    fun eventViewModel_saveButtonClicked_emptyTitle_noUseCaseCall_errorMessage() {
        // Arrange
        viewModel.eventTitle.value = ""
        val errorMessageObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.inputErrorMessageID.observeForever(errorMessageObserver)

        // Act
        viewModel.onSaveButtonClicked()

        // Assert
        verify { saveEventUseCase wasNot called }
        verify { errorMessageObserver.onChanged(any()) }
    }

    @Test
    fun eventViewModel_saveButtonClicked__CallUseCaseWithCorrectParams() {
        // Arrange
        val params = slot<SaveEventUseCase.Params>()
        every { saveEventUseCase.execute(capture(params)) } returns Completable.complete()

        // Act
        viewModel.onSaveButtonClicked()

        // Assert
        verify { saveEventUseCase.execute(any()) }
        with(params.captured.event) {
            assertEquals(eventDateTime, dateTime)
            assertEquals(eventTitle, title)
            assertEquals(eventNotes, notes)
        }
    }

    @Test
    fun eventViewModel_saveButtonClicked_callUseCaseAndNotifiesSuccess() {
        // Arrange
        val params = slot<SaveEventUseCase.Params>()
        every { saveEventUseCase.execute(capture(params)) } returns Completable.complete()

        val errorObserver = mockk<Observer<String>>(relaxed = true)
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.eventSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.onSaveButtonClicked()

        // Assert
        verify { successObserver.onChanged(any()) }
        verify { errorObserver wasNot called }
    }

    @Test
    fun eventViewModel_saveButtonClicked_callUseCaseAndNotifiesFailure() {
        // Arrange
        val params = slot<SaveEventUseCase.Params>()
        every { saveEventUseCase.execute(capture(params)) } returns Completable.error(IOException())

        val errorObserver = mockk<Observer<String>>(relaxed = true)
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.eventSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.onSaveButtonClicked()

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { successObserver wasNot called }
    }

    @Test
    fun eventViewModel_setEvent_updatesAllAttributes() {
        // Arrange
        val newEvent = EventFactory.makeEvent()

        val dateObserver = mockk<Observer<LocalDateTime>>(relaxed = true)
        val newDate = slot<LocalDateTime>()
        every { dateObserver.onChanged(capture(newDate)) } just Runs

        val dateStringObserver = mockk<Observer<String>>(relaxed = true)
        val newDateString = slot<String>()
        every { dateStringObserver.onChanged(capture(newDateString)) } just Runs

        val titleObserver = mockk<Observer<String>>(relaxed = true)
        val newTitle = slot<String>()
        every { titleObserver.onChanged(capture(newTitle)) } just Runs

        val notesObserver = mockk<Observer<String>>(relaxed = true)
        val newNotes = slot<String>()
        every { notesObserver.onChanged(capture(newNotes)) } just Runs

        viewModel.eventDate.observeForever(dateObserver)
        viewModel.eventDateString.observeForever(dateStringObserver)
        viewModel.eventTitle.observeForever(titleObserver)
        viewModel.eventNotes.observeForever(notesObserver)

        // Act
        viewModel.setEvent(newEvent)

        // Assert
        verify { dateObserver.onChanged(any()) }
        verify { dateStringObserver.onChanged(any()) }
        verify { titleObserver.onChanged(any()) }
        verify { notesObserver.onChanged(any()) }

        assertEquals(newEvent.dateTime, newDate.captured)
        assertEquals(newEvent.title, newTitle.captured)
        assertEquals(newEvent.notes, newNotes.captured)
    }

    @Test
    fun eventViewModel_resetEvent_allCleared() {
        // Arrange
        val dateObserver = mockk<Observer<LocalDateTime>>(relaxed = true)
        val titleObserver = mockk<Observer<String?>>(relaxed = true)
        val notesObserver = mockk<Observer<String>>(relaxed = true)
        val eventObserver = mockk<Observer<Event>>(relaxed = true)

        viewModel.eventDate.observeForever(dateObserver)
        viewModel.eventTitle.observeForever(titleObserver)
        viewModel.eventNotes.observeForever(notesObserver)
        viewModel.event.observeForever(eventObserver)

        // Act
        viewModel.resetViewModel()

        // Assert
        assertNotEquals(viewModel.eventDate, eventDateTime)
        assertTrue(viewModel.eventNotes.value.isNullOrEmpty())
        assertTrue(viewModel.eventTitle.value.isNullOrEmpty())
        assertTrue(isEmptyEvent(viewModel.event.value!!))
    }

    private fun isEmptyEvent(event: Event): Boolean {
        with(event) {
            return title.isNullOrEmpty() && notes.isNullOrEmpty() && emotionAvatar == null && details.isEmpty()
        }
    }

    @Test
    fun eventViewModel_saveImage_callsUseCase() {
        // Arrange
        val params = slot<SaveEmotionAvatarUseCase.Params>()
        every { saveEmotionAvatarUseCase.execute(capture(params)) } returns Completable.complete()

        // Act
        viewModel.saveImage(mockk())

        // Assert
        verify { saveEmotionAvatarUseCase.execute(any()) }
        Assert.assertEquals(viewModel.event.value, params.captured.event)
    }

    @Test
    fun eventViewModel_saveImage_notifiesWhenSaveCompletes() {
        // Arrange
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        val failedObserver = mockk<Observer<String>>(relaxed = true)
        every { saveEmotionAvatarUseCase.execute(any()) } returns Completable.complete()
        viewModel.avatarSavedSuccessFully.observeForever(successObserver)
        viewModel.errorMessage.observeForever(failedObserver)

        // Act
        viewModel.saveImage(mockk())

        // Assert
        verify { successObserver.onChanged(any()) }
        verify { failedObserver wasNot Called }
    }

    @Test
    fun eventViewModel_saveImage_notifiesWhenSaveFails() {
        // Arrange
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        val failedObserver = mockk<Observer<String>>(relaxed = true)
        val errorMessage = "Something failed"

        every { saveEmotionAvatarUseCase.execute(any()) } returns Completable.error(RuntimeException(errorMessage))

        viewModel.avatarSavedSuccessFully.observeForever(successObserver)
        viewModel.errorMessage.observeForever(failedObserver)

        // Act
        viewModel.saveImage(mockk())

        // Assert
        verify { failedObserver.onChanged(errorMessage) }
        verify { successObserver wasNot Called }
    }
}