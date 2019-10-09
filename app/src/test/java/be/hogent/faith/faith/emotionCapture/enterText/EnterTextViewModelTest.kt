package be.hogent.faith.faith.emotionCapture.enterText

import android.graphics.Color
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.TestUtils
import be.hogent.faith.service.usecases.LoadTextDetailUseCase
import io.mockk.called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableSingleObserver
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.lang.RuntimeException

class EnterTextViewModelTest {
    private val loadTextDetailUseCase = mockk<LoadTextDetailUseCase>(relaxed = true)

    private lateinit var viewModel: EnterTextViewModel

    private val text = "Hello world"

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = EnterTextViewModel(loadTextDetailUseCase)

        viewModel.text.observeForever(mockk(relaxed = true))
    }

    @Test
    fun enterTextVM_pickNewTextColor_isSelected() {
        viewModel.pickTextColor(Color.BLACK)
        Assert.assertEquals(Color.BLACK, TestUtils.getValue(viewModel.selectedTextColor))

        viewModel.pickTextColor(Color.RED)
        Assert.assertEquals(Color.RED, TestUtils.getValue(viewModel.selectedTextColor))
    }

    @Test
    fun enterTextVM_pickNewFontSize_isSelected() {
        viewModel.pickFontSize(EnterTextViewModel.FontSize.NORMAL)
        Assert.assertEquals(
            EnterTextViewModel.FontSize.NORMAL,
            TestUtils.getValue(viewModel.selectedFontSize)
        )

        viewModel.pickFontSize(EnterTextViewModel.FontSize.LARGE)
        Assert.assertEquals(
            EnterTextViewModel.FontSize.LARGE,
            TestUtils.getValue(viewModel.selectedFontSize)
        )
    }

    @Test
    fun enterTextVM_onBoldClicked_callsListeners() {
        val observer = mockk<Observer<Boolean?>>(relaxed = true)
        viewModel.boldClicked.observeForever(observer)

        viewModel.onBoldClicked()

        verify { observer.onChanged(any()) }
    }

    @Test
    fun enterTextVM_onItalicClicked_callsListeners() {
        val observer = mockk<Observer<Boolean?>>(relaxed = true)
        viewModel.italicClicked.observeForever(observer)

        viewModel.onItalicClicked()

        verify { observer.onChanged(any()) }
    }

    @Test
    fun enterTextVM_onUnderlineClicked_callsListeners() {
        val observer = mockk<Observer<Boolean?>>(relaxed = true)
        viewModel.underlineClicked.observeForever(observer)

        viewModel.onUnderlineClicked()

        verify { observer.onChanged(any()) }
    }

    @Test
    fun enterTextVM_loadTextUC_updatesText() {
        // Arrange
        val saveFile = File("fake/path")
        val textDetail = TextDetail(saveFile, "name")
        val params = slot<LoadTextDetailUseCase.LoadTextParams>()
        val resultObserver = slot<DisposableSingleObserver<String>>()
        val textObserver = mockk<Observer<String>>(relaxed = true)

        viewModel.text.observeForever(textObserver)

        // Act
        viewModel.loadExistingTextDetail(textDetail)
        verify { loadTextDetailUseCase.execute(capture(params), capture(resultObserver)) }
        resultObserver.captured.onSuccess(text)

        // Assert
        verify { textObserver.onChanged(text) }
    }
    @Test
    fun enterTextVM_loadTextUseCaseFails_updatesErrorMessage() {
        // Arrange
        val saveFile = File("fake/path")
        val textDetail = TextDetail(saveFile, "name")

        val resultObserver = slot<DisposableSingleObserver<String>>()
        val textObserver = mockk<Observer<String>>(relaxed = true)
        val errorObserver = mockk<Observer<Int>>(relaxed = true)

        viewModel.text.observeForever(textObserver)
        viewModel.errorMessage.observeForever(errorObserver)

        // Act
        viewModel.loadExistingTextDetail(textDetail)
        verify { loadTextDetailUseCase.execute(any(), capture(resultObserver)) }
        resultObserver.captured.onError(RuntimeException())

        // Assert
        verify { textObserver wasNot called }
        verify { errorObserver.onChanged(any()) }
    }
}