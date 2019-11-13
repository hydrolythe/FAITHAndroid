package be.hogent.faith.faith.details.audio

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.faith.di.appModule
import be.hogent.faith.faith.testModule
import be.hogent.faith.service.usecases.audioDetail.CreateAudioDetailUseCase
import io.mockk.Called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableSingleObserver
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get

class AudioViewModelUseCaseTests : KoinTest {
    private lateinit var viewModel: AudioViewModel
    private val createAudioDetailUseCase = mockk<CreateAudioDetailUseCase>(relaxed = true)

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        startKoin { modules(listOf(appModule, testModule)) }
        viewModel = AudioViewModel(createAudioDetailUseCase, get())
    }

    @After
    fun takeDown() {
        stopKoin()
    }

    @Test
    fun eventViewModel_onSaveClicked_callsUseCase() {
        // Arrange
        val params = slot<CreateAudioDetailUseCase.Params>()
        val observer = slot<DisposableSingleObserver<AudioDetail>>()

        // Act
        viewModel.onSaveClicked()

        verify { createAudioDetailUseCase.execute(capture(params), capture(observer)) }
    }

    @Test
    fun eventViewModel_onSaveClicked_updatesDetailWhenUseCaseCompletes() {
        // Arrange
        val detailObserver = mockk<Observer<AudioDetail>>(relaxed = true)
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val observer = slot<DisposableSingleObserver<AudioDetail>>()
        val createdDetail = mockk<AudioDetail>()

        viewModel.savedDetail.observeForever(detailObserver)
        viewModel.errorMessage.observeForever(errorObserver)

        // Act
        viewModel.onSaveClicked()
        verify { createAudioDetailUseCase.execute(any(), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onSuccess(createdDetail)

        // Assert
        verify { detailObserver.onChanged(createdDetail) }
        verify { errorObserver wasNot Called }
    }

    @Test
    fun eventViewModel_saveAudio_notifiesWithErrorMessageWhenUseCaseFails() {
        // Arrange
        val detailObserver = mockk<Observer<AudioDetail>>(relaxed = true)
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val observer = slot<DisposableSingleObserver<AudioDetail>>()

        viewModel.savedDetail.observeForever(detailObserver)
        viewModel.errorMessage.observeForever(errorObserver)

        // Act
        viewModel.onSaveClicked()
        verify { createAudioDetailUseCase.execute(any(), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { detailObserver wasNot Called }
    }
}