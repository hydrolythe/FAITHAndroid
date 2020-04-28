package be.hogent.faith.faith.backpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.faith.backpackScreen.BackpackViewModel
import be.hogent.faith.service.usecases.backpack.GetBackPackDataUseCase
import be.hogent.faith.service.usecases.detailscontainer.LoadDetailFileUseCase
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
import io.mockk.Called
import io.mockk.called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableCompletableObserver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BackpackViewModelSaveDrawingTest {

    private lateinit var viewModel: BackpackViewModel
    private val saveDrawingUseCase =
        mockk<SaveDetailsContainerDetailUseCase<Backpack>>(relaxed = true)
    private val getBackPackFilesUseCase = mockk<GetBackPackDataUseCase>(relaxed = true)
    private val backpack = mockk<Backpack>(relaxed = true)
    private val loadDetailFileUseCase = mockk<LoadDetailFileUseCase<Backpack>>(relaxed = true)
    private val detail = mockk<DrawingDetail>()
    private val user = mockk<User>(relaxed = true)

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = BackpackViewModel(
            saveDrawingUseCase,
            mockk(),
            backpack,
            loadDetailFileUseCase,
            getBackPackFilesUseCase
        )
    }

    @Test
    fun backpackViewModel_saveDrawing_callsUseCase() {
        val params = slot<SaveDetailsContainerDetailUseCase.Params>()

        viewModel.saveDrawingDetail(user, detail)
        verify { saveDrawingUseCase.execute(capture(params), any()) }

        assertEquals(detail, params.captured.detail)
    }

    @Test
    fun backpackViewModel_saveDrawing_notifiesUseCaseSuccess() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.infoMessage.observeForever(successObserver)

        // Act
        viewModel.saveDrawingDetail(user, detail)
        verify { saveDrawingUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onComplete()

        // Assert
        verify { successObserver.onChanged(R.string.save_drawing_success) }
        verify { errorObserver wasNot called }
    }

    @Test
    fun backpackViewModel_saveDrawing_notifiesUseCaseFailure() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.infoMessage.observeForever(successObserver)

        // Act
        viewModel.saveDrawingDetail(user, detail)
        verify { saveDrawingUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { successObserver wasNot Called }
    }
}