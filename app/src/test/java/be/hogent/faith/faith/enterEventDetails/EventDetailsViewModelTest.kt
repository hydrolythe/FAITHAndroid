package be.hogent.faith.faith.enterEventDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.Event
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EventDetailsViewModelTest {
    private lateinit var viewModel: EventDetailsViewModel

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = EventDetailsViewModel(mockk())
    }

    @Test
    fun eventDetailsVM_updateEvent_callsListeners() {
        // Arrange
        val observer = mockk<Observer<Event>>(relaxed = true)
        viewModel.event.observeForever(observer)

        // Act
        viewModel.updateEvent()

        // Assert
        verify { observer.onChanged(any()) }
    }
}