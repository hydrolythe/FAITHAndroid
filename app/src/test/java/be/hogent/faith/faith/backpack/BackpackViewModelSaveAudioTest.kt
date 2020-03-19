package be.hogent.faith.faith.backpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.faith.backpackScreen.BackpackViewModel
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesDummyUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackAudioDetailUseCase
import io.mockk.Called
import io.mockk.called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableCompletableObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals

class BackpackViewModelSaveAudioTest {
    private lateinit var viewModel: BackpackViewModel
    private val saveAudioUseCase = mockk<SaveBackpackAudioDetailUseCase>(relaxed = true)
    private val getBackPackFilesDummyUseCase = mockk<GetBackPackFilesDummyUseCase>(relaxed = true)
    private val detail = mockk<AudioDetail>()
    private val user: User = mockk()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = BackpackViewModel(
            mockk(),
            saveAudioUseCase,
            mockk(),
            mockk(),
            getBackPackFilesDummyUseCase
        )
    }

    @Test
    fun backpackViewModel_saveAudio_callsUseCase() {
        val params = slot<SaveBackpackAudioDetailUseCase.Params>()

        viewModel.saveAudioDetail(user, detail)
        verify { saveAudioUseCase.execute(capture(params), any()) }

        assertEquals(detail, params.captured.audioDetail)
    }

    @Test
    fun backpackViewModel_saveAudio_notifiesUseCaseSuccess() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.audioDetailSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.saveAudioDetail(user, detail)
        verify { saveAudioUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onComplete()

        // Assert
        verify { successObserver.onChanged(R.string.save_audio_success) }
        verify { errorObserver wasNot called }
    }

    @Test
    fun backpackViewModel_saveAudio_notifiesUseCaseFailure() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.audioDetailSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.saveAudioDetail(user, detail)
        verify { saveAudioUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { successObserver wasNot Called }
    }
}