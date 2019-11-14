package be.hogent.faith.faith.emotionCapture.enterEventDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.di.appModule
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.EventFactory
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

class EventViewModelTest {
    private lateinit var viewModel: EventViewModel

    private val eventTitle = DataFactory.randomString()
    private val eventNotes = DataFactory.randomString()
    private val eventDateTime = DataFactory.randomDateTime()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = EventViewModel(
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            mockk()
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
}