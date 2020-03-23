package be.hogent.faith.faith.library.eventList

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.TestUtils.getValue
import be.hogent.faith.service.usecases.event.GetEventsUseCase
import be.hogent.faith.util.factory.DetailFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.reactivex.subscribers.DisposableSubscriber
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Month
import org.threeten.bp.ZoneOffset

class EventListViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: EventListViewModel

    private val event_1_jan_photo = Event(
        dateTime = LocalDateTime.of(2020, Month.JANUARY, 10, 10, 30),
        title = "title 1"
    ).apply {
        addDetail(DetailFactory.makePhotoDetail())
    }
    private val event_2_jan_text = Event(
        dateTime = LocalDateTime.of(2020, Month.JANUARY, 15, 10, 30),
        title = "title 2"
    ).apply {
        addDetail(DetailFactory.makeTextDetail())
    }
    private val event_3_feb_drawing = Event(
        dateTime = LocalDateTime.of(2020, Month.FEBRUARY, 15, 10, 30),
        title = "title 3"
    ).apply {
        addDetail(DetailFactory.makeDrawingDetail())
    }
    private val event_4_july_audio = Event(
        dateTime = LocalDateTime.of(2020, Month.MARCH, 15, 10, 30),
        title = "title 4"
    ).apply {
        addDetail(DetailFactory.makeAudioDetail())
    }

    private val events =
        listOf(event_4_july_audio, event_1_jan_photo, event_2_jan_text, event_3_feb_drawing)

    // We need to immediatly observe on the [EventListViewModel.filteredEvents] or otherwise
    // the MediatorLiveData will not start processing updates from its sources.
    private val eventsObserver = Observer<List<Event>> { }

    private val user = mockk<User>()
    private val getEventsUseCase = mockk<GetEventsUseCase>()
    private val eventsUseCaseSubscriber = slot<DisposableSubscriber<List<Event>>>()

    @Before
    fun setUp() {
        setUpGetEventsUseCase()
        viewModel = EventListViewModel(
            user = user,
            getEventsUseCase = getEventsUseCase
        )
        eventsUseCaseSubscriber.captured.onNext(events)
        viewModel.filteredEvents.observeForever(eventsObserver)
    }

    private fun setUpGetEventsUseCase() {
        every {
            getEventsUseCase.execute(
                any(),
                capture(eventsUseCaseSubscriber)
            )
        } returns Unit
    }

    @After
    fun tearDown() {
        viewModel.filteredEvents.removeObserver(eventsObserver)
    }

    @Test
    fun `by default all events are shown, meaning no filters are active`() {
        // Assert
        val filteredEvents = getValue(viewModel.filteredEvents)

        assertEquals(4, filteredEvents.size)
    }

    @Test
    fun `when clicking the text detail filter, it is enabled and only events with at least one text detail are shown`() {
        // Act
        viewModel.onFilterTextClicked()
        val filteredEvents = getValue(viewModel.filteredEvents)

        assertEquals(1, filteredEvents.size)
        assertTrue(filteredEvents.contains(event_2_jan_text))
    }

    @Test
    fun `when clicking the photo detail filter, it is enabled and only events with at least one photo detail are shown`() {
        // Act
        viewModel.onFilterPhotosClicked()
        val filteredEvents = getValue(viewModel.filteredEvents)

        assertEquals(1, filteredEvents.size)
        assertTrue(filteredEvents.contains(event_1_jan_photo))
    }

    @Test
    fun `when clicking the drawing detail filter, it is enabled and only events with at least 1 drawing detail are shown`() {
        // Act
        viewModel.onFilterDrawingClicked()
        val filteredEvents = getValue(viewModel.filteredEvents)

        assertEquals(1, filteredEvents.size)
        assertTrue(filteredEvents.contains(event_3_feb_drawing))
    }

    @Test
    fun `when clicking the audio detail filter, it is enabled and only events with at least one audio detail are shown`() {
        // Act
        viewModel.onFilterAudioClicked()
        val filteredEvents = getValue(viewModel.filteredEvents)
        println("events received $filteredEvents")

        assertEquals(1, filteredEvents.size)
        assertTrue(filteredEvents.contains(event_4_july_audio))
    }

    @Test
    fun `when setting the start and end date filters, only events that in between those dates are shown`() {
        // Arrange
        viewModel.setDateRange(LocalDate.of(2020, Month.JANUARY, 1).atStartOfDay().toInstant(
            ZoneOffset.of("+01:00")).toEpochMilli(), LocalDate.of(2020, Month.FEBRUARY, 1).atStartOfDay().toInstant(
            ZoneOffset.of("+01:00")).toEpochMilli())
        // Act
        val filteredEvents = getValue(viewModel.filteredEvents)

        // Assert
        assertEquals(2, filteredEvents.size)
        assertTrue(filteredEvents.contains(event_1_jan_photo))
        assertTrue(filteredEvents.contains(event_2_jan_text))
    }

    @Test
    fun `when combining a text and a date filter, only events with text details from the given date range are shown`() {
        // Arrange
        viewModel.setDateRange(LocalDate.of(2020, Month.JANUARY, 1).atStartOfDay().toInstant(
            ZoneOffset.of("+01:00")).toEpochMilli(), LocalDate.of(2020, Month.FEBRUARY, 1).atStartOfDay().toInstant(
            ZoneOffset.of("+01:00")).toEpochMilli())
        viewModel.onFilterTextClicked()

        // Act
        val filteredEvents = getValue(viewModel.filteredEvents)

        // Assert
        assertEquals(1, filteredEvents.size)
        assertTrue(filteredEvents.contains(event_2_jan_text))
    }

    @Test
    fun `when disabling a filter all events are shown again`() {
        // Arrange
        viewModel.onFilterTextClicked() // enable

        // Act
        viewModel.onFilterTextClicked() // disable
        val filteredEvents = getValue(viewModel.filteredEvents)

        // Assert
        assertEquals(4, filteredEvents.size)
    }
}