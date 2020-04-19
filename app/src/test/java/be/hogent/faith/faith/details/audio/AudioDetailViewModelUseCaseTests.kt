package be.hogent.faith.faith.details.audio

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.faith.di.appModule
import be.hogent.faith.faith.testModule
import be.hogent.faith.service.usecases.detailscontainer.LoadDetailFileUseCase
import be.hogent.faith.service.usecases.detail.audioDetail.CreateAudioDetailUseCase
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

class AudioDetailViewModelUseCaseTests : KoinTest {
    private lateinit var detailViewModel: AudioDetailViewModel
    private val createAudioDetailUseCase = mockk<CreateAudioDetailUseCase>(relaxed = true)
    private val loadDetailFileUseCase = mockk<LoadDetailFileUseCase>(relaxed = true)

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        startKoin { modules(listOf(appModule, testModule)) }
        detailViewModel = AudioDetailViewModel(createAudioDetailUseCase, get(), loadDetailFileUseCase)
        detailViewModel.initialiseState()
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
        detailViewModel.onSaveClicked()

        verify { createAudioDetailUseCase.execute(capture(params), capture(observer)) }
    }

    @Test
    fun eventViewModel_onSaveClicked_updatesDetailWhenUseCaseCompletes() {
        // Arrange
        val detailObserver = mockk<Observer<AudioDetail>>(relaxed = true)
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val observer = slot<DisposableSingleObserver<AudioDetail>>()
        val createdDetail = mockk<AudioDetail>()

        detailViewModel.savedDetail.observeForever(detailObserver)
        detailViewModel.errorMessage.observeForever(errorObserver)

        // Act
        detailViewModel.onSaveClicked()
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

        detailViewModel.savedDetail.observeForever(detailObserver)
        detailViewModel.errorMessage.observeForever(errorObserver)

        // Act
        detailViewModel.onSaveClicked()
        verify { createAudioDetailUseCase.execute(any(), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { detailObserver wasNot Called }
    }
}