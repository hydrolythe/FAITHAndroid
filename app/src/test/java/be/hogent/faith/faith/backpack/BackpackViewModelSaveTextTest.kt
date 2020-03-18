package be.hogent.faith.faith.backpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.backpackScreen.BackpackViewModel
import be.hogent.faith.service.usecases.backpack.SaveBackpackTextDetailUseCase
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableCompletableObserver
import org.junit.Assert.assertEquals
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesDummyUseCase
import io.mockk.Called
import io.mockk.called
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BackpackViewModelSaveTextTest {
    private lateinit var viewModel: BackpackViewModel
    private val saveTextUseCase = mockk<SaveBackpackTextDetailUseCase>(relaxed = true)
    private val getBackPackFilesDummyUseCase = mockk<GetBackPackFilesDummyUseCase>(relaxed = true)
    private val detail = mockk<TextDetail>()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = BackpackViewModel(
                saveTextUseCase,
                mockk(),
                mockk(),
                mockk(),
                mockk(),
                getBackPackFilesDummyUseCase
        )
    }

    @Test
    fun backpackViewModel_saveText_callsUseCase() {
        val params = slot<SaveBackpackTextDetailUseCase.Params>()

        viewModel.saveTextDetail(detail)
        verify { saveTextUseCase.execute(capture(params), any()) }

        assertEquals(detail, params.captured.textDetail)
    }

    @Test
    fun backpackViewModel_saveText_notifiesUseCaseSuccess() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.textDetailSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.saveTextDetail(detail)
        verify { saveTextUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onComplete()

        // Assert
        verify { successObserver.onChanged(R.string.save_text_success) }
        verify { errorObserver wasNot called }
    }

    @Test
    fun backpackViewModel_saveText_notifiesUseCaseFailure() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.textDetailSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.saveTextDetail(detail)
        verify { saveTextUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { successObserver wasNot Called }
    }
}