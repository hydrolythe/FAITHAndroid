package be.hogent.faith.faith.details.text.create

import android.graphics.Color
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.faith.TestUtils
import be.hogent.faith.service.usecases.textDetail.CreateTextDetailUseCase
import be.hogent.faith.service.usecases.textDetail.LoadTextDetailUseCase
import be.hogent.faith.service.usecases.textDetail.OverwriteTextDetailUseCase
import be.hogent.faith.util.factory.DetailFactory
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableSingleObserver
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TextDetailViewModelTest {
    private val loadTextDetailUseCase = mockk<LoadTextDetailUseCase>(relaxed = true)
    private val createTextDetailUseCase = mockk<CreateTextDetailUseCase>(relaxed = true)
    private val overwriteTextDetailUseCase = mockk<OverwriteTextDetailUseCase>(relaxed = true)

    private lateinit var detailViewModel: TextDetailViewModel

    private val detailText = "Text in the detail"

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        detailViewModel =
            TextDetailViewModel(
                loadTextDetailUseCase,
                createTextDetailUseCase,
                overwriteTextDetailUseCase
            )

        detailViewModel.text.observeForever(mockk(relaxed = true))
    }

    @Test
    fun enterTextVM_pickNewTextColor_isSelected() {
        detailViewModel.pickTextColor(Color.BLACK)
        Assert.assertEquals(Color.BLACK, TestUtils.getValue(detailViewModel.selectedTextColor))

        detailViewModel.pickTextColor(Color.RED)
        Assert.assertEquals(Color.RED, TestUtils.getValue(detailViewModel.selectedTextColor))
    }

    @Test
    fun enterTextVM_pickNewFontSize_isSelected() {
        detailViewModel.pickFontSize(TextDetailViewModel.FontSize.NORMAL)
        Assert.assertEquals(
            TextDetailViewModel.FontSize.NORMAL,
            TestUtils.getValue(detailViewModel.selectedFontSize)
        )

        detailViewModel.pickFontSize(TextDetailViewModel.FontSize.LARGE)
        Assert.assertEquals(
            TextDetailViewModel.FontSize.LARGE,
            TestUtils.getValue(detailViewModel.selectedFontSize)
        )
    }

    @Test
    fun enterTextVM_onBoldClicked_callsListeners() {
        val observer = mockk<Observer<Boolean?>>(relaxed = true)
        detailViewModel.boldClicked.observeForever(observer)

        detailViewModel.onBoldClicked()

        verify { observer.onChanged(any()) }
    }

    @Test
    fun enterTextVM_onItalicClicked_callsListeners() {
        val observer = mockk<Observer<Boolean?>>(relaxed = true)
        detailViewModel.italicClicked.observeForever(observer)

        detailViewModel.onItalicClicked()

        verify { observer.onChanged(any()) }
    }

    @Test
    fun enterTextVM_onUnderlineClicked_callsListeners() {
        val observer = mockk<Observer<Boolean?>>(relaxed = true)
        detailViewModel.underlineClicked.observeForever(observer)

        detailViewModel.onUnderlineClicked()

        verify { observer.onChanged(any()) }
    }

    @Test
    fun enterTextVM_loadTextUC_updatesText() {
        // Arrange
        val textDetail = DetailFactory.makeTextDetail()
        val params = slot<LoadTextDetailUseCase.LoadTextParams>()
        val resultObserver = slot<DisposableSingleObserver<String>>()
        val textObserver = mockk<Observer<String>>(relaxed = true)

        detailViewModel.initialText.observeForever(textObserver)

        // Act
        detailViewModel.loadExistingDetail(textDetail)
        verify { loadTextDetailUseCase.execute(capture(params), capture(resultObserver)) }
        resultObserver.captured.onSuccess(detailText)

        // Assert
        verify { textObserver.onChanged(detailText) }
    }

    @Test
    fun enterTextVM_loadTextUseCaseFails_updatesErrorMessage() {
        // Arrange
        val textDetail = DetailFactory.makeTextDetail()
        val resultObserver = slot<DisposableSingleObserver<String>>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)

        detailViewModel.errorMessage.observeForever(errorObserver)

        // Act
        detailViewModel.loadExistingDetail(textDetail)
        verify { loadTextDetailUseCase.execute(any(), capture(resultObserver)) }
        resultObserver.captured.onError(RuntimeException())

        // Assert
        verify { errorObserver.onChanged(any()) }
    }
}