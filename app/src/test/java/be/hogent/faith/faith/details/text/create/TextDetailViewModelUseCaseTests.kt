package be.hogent.faith.faith.details.text.create

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.di.appModule
import be.hogent.faith.faith.testModule
import be.hogent.faith.service.usecases.detail.textDetail.CreateTextDetailUseCase
import be.hogent.faith.service.usecases.detail.textDetail.LoadTextDetailUseCase
import be.hogent.faith.service.usecases.detail.textDetail.OverwriteTextDetailUseCase
import io.mockk.Called
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.rxjava3.observer.DisposableCompletableObserver
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

class TextDetailViewModelUseCaseTests : KoinTest {
    private lateinit var viewModel: TextDetailViewModel
    private val createTextUseCase = mockk<CreateTextDetailUseCase>(relaxed = true)
    private val loadTextDetailUseCase = mockk<LoadTextDetailUseCase>(relaxed = true)
    private val overwriteTextDetailUseCase = mockk<OverwriteTextDetailUseCase>(relaxed = true)

    private val detailText = "Text in the detail"

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        startKoin { modules(listOf(appModule, testModule)) }
        viewModel =
            TextDetailViewModel(
                loadTextDetailUseCase,
                createTextUseCase,
                overwriteTextDetailUseCase
            )
    }

    @After
    fun takeDown() {
        stopKoin()
    }

    @Test
    fun enterTextVM_loadTextUC_updatesText() {
        // Arrange
        val existingDetail = mockk<TextDetail>()
        val useCaseParams = slot<LoadTextDetailUseCase.LoadTextParams>()
        val useCaseObserver = slot<DisposableSingleObserver<String>>()
        val textObserver = mockk<Observer<String>>(relaxed = true)

        viewModel.initialText.observeForever(textObserver)

        // Act
        viewModel.loadExistingDetail(existingDetail)
        verify { loadTextDetailUseCase.execute(capture(useCaseParams), capture(useCaseObserver)) }
        useCaseObserver.captured.onSuccess(detailText)

        // Assert
        assertEquals(existingDetail, useCaseParams.captured.textDetail)
        verify { textObserver.onChanged(detailText) }
    }

    @Test
    fun enterTextVM_loadTextUseCaseFails_updatesErrorMessage() {
        // Arrange
        val existingDetail = mockk<TextDetail>()
        val resultObserver = slot<DisposableSingleObserver<String>>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val initialTextObserver = mockk<Observer<String>>(relaxed = true)

        viewModel.initialText.observeForever(initialTextObserver)
        viewModel.errorMessage.observeForever(errorObserver)

        // Act
        viewModel.loadExistingDetail(existingDetail)
        verify { loadTextDetailUseCase.execute(any(), capture(resultObserver)) }
        resultObserver.captured.onError(RuntimeException())

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { initialTextObserver wasNot called }
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
    fun textDetailViewModel_onSaveClicked_noExistingDetail_callsCreateUseCase() {
        // Arrange
        val params = slot<CreateTextDetailUseCase.Params>()
        val observer = slot<DisposableSingleObserver<TextDetail>>()
        viewModel.setText("We need to input text so we can save")

        // Act
        viewModel.onSaveClicked()

        verify { createTextUseCase.execute(capture(params), capture(observer)) }
    }

    @Test
    fun textDetailViewModel_onSaveClicked_existingDetail_callsOverwriteUseCase() {
        // Arrange
        val params = slot<OverwriteTextDetailUseCase.Params>()
        val observer = slot<DisposableCompletableObserver>()
        val existingDetail = mockk<TextDetail>()
        viewModel.loadExistingDetail(existingDetail)
        viewModel.setText(detailText)

        // Act
        viewModel.onSaveClicked()

        // Assert
        verify { overwriteTextDetailUseCase.execute(capture(params), capture(observer)) }
        assertEquals(existingDetail, params.captured.detail)
        assertEquals(detailText, params.captured.text)
    }

    @Test
    fun textDetailViewModel_onSaveClicked_noExistingDetail_callsGetMetaDataOnUseCaseCompleted() {
        // Arrange
        val getDetailsMetaDataObserver = mockk<Observer<Unit>>(relaxed = true)
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val observer = slot<DisposableSingleObserver<TextDetail>>()
        val createdDetail = mockk<TextDetail>()
        viewModel.setText("We need to input text so we can save")

        viewModel.getDetailMetaData.observeForever(getDetailsMetaDataObserver)
        viewModel.errorMessage.observeForever(errorObserver)

        // Act
        viewModel.onSaveClicked()
        verify { createTextUseCase.execute(any(), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onSuccess(createdDetail)

        // Assert
        verify { getDetailsMetaDataObserver.onChanged(any()) }
        verify { errorObserver wasNot Called }
    }

    @Test
    fun textDetailViewModel_onSaveClicked_existingDetail_updatesDetailOnUseCaseCompleted() {
        // Arrange
        val detailObserver = mockk<Observer<TextDetail>>(relaxed = true)
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val existingDetail = mockk<TextDetail>()
        viewModel.loadExistingDetail(existingDetail)
        viewModel.setText("We need to input text so we can save")

        viewModel.savedDetail.observeForever(detailObserver)
        viewModel.errorMessage.observeForever(errorObserver)

        // Act
        viewModel.onSaveClicked()
        verify { overwriteTextDetailUseCase.execute(any(), capture(useCaseObserver)) }
        // Make the UC-handler call the success handler
        useCaseObserver.captured.onComplete()

        // Assert
        verify { detailObserver.onChanged(existingDetail) }
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