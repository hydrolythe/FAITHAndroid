package be.hogent.faith.faith.emotionCapture.enterEventDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.SaveEmotionAvatarUseCase
import be.hogent.faith.service.usecases.SaveEventAudioUseCase
import be.hogent.faith.service.usecases.SaveEventDrawingUseCase
import be.hogent.faith.service.usecases.SaveEventPhotoUseCase
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.EventFactory
import io.mockk.Called
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableCompletableObserver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

class EventViewModelTest {
    private lateinit var viewModel: EventViewModel
    private val saveEmotionAvatarUseCase = mockk<SaveEmotionAvatarUseCase>(relaxed = true)
    private val savePhotoUseCase = mockk<SaveEventPhotoUseCase>(relaxed = true)
    private val saveDrawingUseCase = mockk<SaveEventDrawingUseCase>(relaxed = true)
    private val saveAudioUseCase = mockk<SaveEventAudioUseCase>(relaxed = true)

    private val eventTitle = DataFactory.randomString()
    private val eventNotes = DataFactory.randomString()
    private val eventDateTime = DataFactory.randomDateTime()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = EventViewModel(
            saveEmotionAvatarUseCase,
            savePhotoUseCase,
            saveAudioUseCase,
            saveDrawingUseCase
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
    fun eventViewModel_saveEmotionAvatar_callsUseCase() {
        // Arrange
        val params = slot<SaveEmotionAvatarUseCase.Params>()
        val observer = slot<DisposableCompletableObserver>()

        // Act
        viewModel.saveEmotionAvatarImage(mockk())
        verify { saveEmotionAvatarUseCase.execute(capture(params), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onComplete()

        // Assert
        assertEquals(viewModel.event.value, params.captured.event)
    }

    @Test
    fun eventViewModel_saveEmotionAvatar_notifiesWhenSaveCompletes() {
        // Arrange
        val observer = slot<DisposableCompletableObserver>()
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        val failedObserver = mockk<Observer<Int>>(relaxed = true)

        viewModel.avatarSavedSuccessFully.observeForever(successObserver)
        viewModel.errorMessage.observeForever(failedObserver)

        // Act
        viewModel.saveEmotionAvatarImage(mockk())
        verify { saveEmotionAvatarUseCase.execute(any(), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onComplete()

        // Assert
        verify { successObserver.onChanged(any()) }
        verify { failedObserver wasNot Called }
    }

    @Test
    fun eventViewModel_saveEmotionAvatar_notifiesWhenSaveFails() {
        // Arrange
        val observer = slot<DisposableCompletableObserver>()
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        val failedObserver = mockk<Observer<Int>>(relaxed = true)

        viewModel.avatarSavedSuccessFully.observeForever(successObserver)
        viewModel.errorMessage.observeForever(failedObserver)

        // Act
        viewModel.saveEmotionAvatarImage(mockk())
        verify { saveEmotionAvatarUseCase.execute(any(), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onError(mockk(relaxed = true))

        // Assert
        verify { failedObserver.onChanged(any()) }
        verify { successObserver wasNot Called }
    }
}