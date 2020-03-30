package be.hogent.faith.faith.backpack

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
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.backpackScreen.BackpackViewModel
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesDummyUseCase

class BackpackViewModelDeleteDetailTest {
    private lateinit var viewModel: BackpackViewModel
    private val deleteBackpackDetailUseCase = mockk<DeleteDetailsContainerDetailUseCase<Backpack>>(relaxed = true)
    private val getBackPackFilesDummyUseCase = mockk<GetBackPackFilesDummyUseCase>(relaxed = true)
    private val detail = mockk<Detail>()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = BackpackViewModel(
                mockk(),
                deleteBackpackDetailUseCase,
                getBackPackFilesDummyUseCase,
                mockk()
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