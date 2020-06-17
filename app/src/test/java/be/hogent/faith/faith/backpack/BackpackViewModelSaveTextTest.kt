package be.hogent.faith.faith.backpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.usecases.detailscontainer.GetDetailsContainerDataUseCase
import be.hogent.faith.service.usecases.detailscontainer.LoadDetailFileUseCase
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
import io.mockk.Called
import io.mockk.called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.rxjava3.observers.DisposableCompletableObserver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BackpackViewModelSaveTextTest {
    private lateinit var viewModel: BackpackViewModel
    private val saveDetailsContainerDetailsUseCase =
        mockk<SaveDetailsContainerDetailUseCase<Backpack>>(relaxed = true)
    private val loadDetailFileUseCase = mockk<LoadDetailFileUseCase<Backpack>>(relaxed = true)
    private val getBackPackFilesUseCase =
        mockk<GetDetailsContainerDataUseCase<Backpack>>(relaxed = true)
    private val backpack = mockk<Backpack>(relaxed = true)
    private val detail = mockk<TextDetail>()
    private val user: User = mockk(relaxed = true)

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = BackpackViewModel(
            saveDetailsContainerDetailsUseCase,
            mockk(),
            backpack,
            loadDetailFileUseCase,
            getBackPackFilesUseCase
        )
    }

    @Test
    fun backpackViewModel_saveText_callsUseCase() {
        val params = slot<SaveDetailsContainerDetailUseCase.Params>()

        viewModel.saveTextDetail(user, detail)
        verify { saveDetailsContainerDetailsUseCase.execute(capture(params), any()) }

        assertEquals(detail, params.captured.detail)
    }

    @Test
    fun backpackViewModel_saveText_notifiesUseCaseSuccess() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.infoMessage.observeForever(successObserver)

        // Act
        viewModel.saveTextDetail(user, detail)
        verify { saveDetailsContainerDetailsUseCase.execute(any(), capture(useCaseObserver)) }
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
        viewModel.infoMessage.observeForever(successObserver)

        // Act
        viewModel.saveTextDetail(user, detail)
        verify { saveDetailsContainerDetailsUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { successObserver wasNot Called }
    }
}