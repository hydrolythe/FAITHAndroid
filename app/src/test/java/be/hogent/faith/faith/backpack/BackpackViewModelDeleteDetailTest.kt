package be.hogent.faith.faith.backpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.detailscontainer.GetDetailsContainerDataUseCase
import be.hogent.faith.service.usecases.detailscontainer.LoadDetailFileUseCase
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import io.mockk.Called
import io.mockk.called
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableCompletableObserver
import org.junit.Assert.assertEquals

class BackpackViewModelDeleteDetailTest {
    private lateinit var viewModel: BackpackViewModel
    private val deleteBackpackDetailUseCase =
        mockk<DeleteDetailsContainerDetailUseCase<Backpack>>(relaxed = true)
    private val loadDetailFileUseCase = mockk<LoadDetailFileUseCase<Backpack>>(relaxed = true)
    private val getBackPackFilesUseCase = mockk<GetDetailsContainerDataUseCase<Backpack>>(relaxed = true)
    private val backpack = mockk<Backpack>(relaxed = true)
    private val detail = mockk<Detail>()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = BackpackViewModel(
            mockk(),
            deleteBackpackDetailUseCase,
            backpack,
            loadDetailFileUseCase,
            getBackPackFilesUseCase
        )
    }

    @Test
    fun backpackViewModel_deleteDetail_callsUseCase() {
        val params = slot<DeleteDetailsContainerDetailUseCase.Params>()

        viewModel.deleteDetail(detail)
        verify { deleteBackpackDetailUseCase.execute(capture(params), any()) }

        assertEquals(detail, params.captured.detail)
    }

    @Test
    fun backpackViewModel_deleteDetail_notifiesUseCaseSuccess() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.infoMessage.observeForever(successObserver)

        // Act
        viewModel.deleteDetail(detail)
        verify { deleteBackpackDetailUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onComplete()

        // Assert
        verify { successObserver.onChanged(R.string.delete_detail_success) }
        verify { errorObserver wasNot called }
    }

    @Test
    fun backpackViewModel_deleteDetail_notifiesUseCaseFailure() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.infoMessage.observeForever(successObserver)

        // Act
        viewModel.deleteDetail(detail)
        verify { deleteBackpackDetailUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { successObserver wasNot Called }
    }
}