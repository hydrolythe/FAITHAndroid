package be.hogent.faith.faith.backpack

import be.hogent.faith.domain.models.detail.VideoDetail
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.mockk.Called
import io.mockk.called
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import io.reactivex.observers.DisposableCompletableObserver
import org.junit.Assert.assertEquals
import androidx.lifecycle.Observer
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import be.hogent.faith.R
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.usecases.detailscontainer.GetDetailsContainerDataUseCase
import be.hogent.faith.service.usecases.detailscontainer.LoadDetailFileUseCase
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase

class BackpackViewModelSaveExternalVideoTest {
    private lateinit var viewModel: BackpackViewModel
    private val saveExternalVideoUseCase =
        mockk<SaveDetailsContainerDetailUseCase<Backpack>>(relaxed = true)
    private val loadDetailFileUseCase = mockk<LoadDetailFileUseCase<Backpack>>(relaxed = true)
    private val getBackPackFilesUseCase =
        mockk<GetDetailsContainerDataUseCase<Backpack>>(relaxed = true)
    private val backpack = mockk<Backpack>(relaxed = true)
    private val detail = mockk<VideoDetail>()
    private val user = mockk<User>(relaxed = true)

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = BackpackViewModel(
            saveExternalVideoUseCase,
            mockk(),
            backpack,
            loadDetailFileUseCase,
            getBackPackFilesUseCase
        )
    }

    @Test
    fun backpackViewModel_saveExternalVideo_callsUseCase() {
        val params = slot<SaveDetailsContainerDetailUseCase.Params>()

        viewModel.saveExternalVideoDetail(user, detail)
        verify { saveExternalVideoUseCase.execute(capture(params), any()) }

        assertEquals(detail, params.captured.detail)
    }

    @Test
    fun backpackViewModel_saveExternalVideo_notifiesUseCaseSuccess() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.infoMessage.observeForever(successObserver)

        // Act
        viewModel.saveExternalVideoDetail(user, detail)
        verify { saveExternalVideoUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onComplete()

        // Assert
        verify { successObserver.onChanged(R.string.save_video_success) }
        verify { errorObserver wasNot called }
    }

    @Test
    fun backpackViewModel_saveExternalVideo_notifiesUseCaseFailure() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.infoMessage.observeForever(successObserver)

        // Act
        viewModel.saveExternalVideoDetail(user, detail)
        verify { saveExternalVideoUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { successObserver wasNot Called }
    }
}