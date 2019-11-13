package be.hogent.faith.faith.details.text.create

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.di.appModule
import be.hogent.faith.faith.testModule
import be.hogent.faith.service.usecases.textDetail.CreateTextDetailUseCase
import be.hogent.faith.service.usecases.textDetail.LoadTextDetailUseCase
import be.hogent.faith.service.usecases.textDetail.OverwriteTextDetailUseCase
import io.mockk.Called
import io.mockk.every
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

class TextDetailViewModelUseCaseTests : KoinTest {
    private lateinit var viewModel: TextDetailViewModel
    private val createTextUseCase = mockk<CreateTextDetailUseCase>(relaxed = true)
    private val loadTextUseCase = mockk<LoadTextDetailUseCase>(relaxed = true)
    private val overwriteTextDetailUseCase = mockk<OverwriteTextDetailUseCase>(relaxed = true)

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        startKoin { modules(listOf(appModule, testModule)) }
        viewModel =
            TextDetailViewModel(loadTextUseCase, createTextUseCase, overwriteTextDetailUseCase)
    }

    @After
    fun takeDown() {
        stopKoin()
    }

    @Test
    fun textDetailViewModel_onSaveClicked_emptyText_showsError() {
        // Arrange
        val errorObserver = mockk<Observer<Int>>()
        every { errorObserver.onChanged(any()) } returns Unit
        viewModel.errorMessage.observeForever(errorObserver)

        // Act
        viewModel.onSaveClicked()

        verify { errorObserver.onChanged(R.string.text_save_error_emptyinput) }
        verify { createTextUseCase.buildUseCaseSingle(any()) wasNot Called }
    }

    @Test
    fun textDetailViewModel_onSaveClicked_callsUseCase() {
        // Arrange
        val params = slot<CreateTextDetailUseCase.Params>()
        val observer = slot<DisposableSingleObserver<TextDetail>>()
        viewModel.setText("We need to input text so we can save")

        // Act
        viewModel.onSaveClicked()

        verify { createTextUseCase.execute(capture(params), capture(observer)) }
    }

    @Test
    fun textDetailViewModel_onSaveClicked_updatesDetailWhenUseCaseCompletes() {
        // Arrange
        val detailObserver = mockk<Observer<TextDetail>>(relaxed = true)
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val observer = slot<DisposableSingleObserver<TextDetail>>()
        val createdDetail = mockk<TextDetail>()
        viewModel.setText("We need to input text so we can save")

        viewModel.savedDetail.observeForever(detailObserver)
        viewModel.errorMessage.observeForever(errorObserver)

        // Act
        viewModel.onSaveClicked()
        verify { createTextUseCase.execute(any(), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onSuccess(createdDetail)

        // Assert
        verify { detailObserver.onChanged(createdDetail) }
        verify { errorObserver wasNot Called }
    }

    @Test
    fun textDetailViewModel_onSaveClicked_notifiesWithErrorMessageWhenUseCaseFails() {
        // Arrange
        val detailObserver = mockk<Observer<TextDetail>>(relaxed = true)
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val useCaseObserver = slot<DisposableSingleObserver<TextDetail>>()
        viewModel.setText("We need to input text so we can save")

        viewModel.savedDetail.observeForever(detailObserver)
        viewModel.errorMessage.observeForever(errorObserver)

        // Act
        viewModel.onSaveClicked()
        verify { createTextUseCase.execute(any(), capture(useCaseObserver)) }
        // Make the UC-handler call the success handler
        useCaseObserver.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { detailObserver wasNot Called }
    }
}