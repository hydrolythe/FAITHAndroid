package be.hogent.faith.faith.emotionCapture.enterEventDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import be.hogent.faith.service.usecases.SaveEmotionAvatarUseCase
import be.hogent.faith.service.usecases.SaveEventAudioUseCase
import be.hogent.faith.service.usecases.SaveEventDrawingUseCase
import be.hogent.faith.service.usecases.SaveEventPhotoUseCase
import be.hogent.faith.util.factory.DataFactory
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule

class EventViewModelSaveDrawingTest {
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
}