package be.hogent.faith.faith.details.text.create

import android.graphics.Color
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.faith.TestUtils
import be.hogent.faith.service.usecases.detail.textDetail.CreateTextDetailUseCase
import be.hogent.faith.service.usecases.detail.textDetail.LoadTextDetailUseCase
import be.hogent.faith.service.usecases.detail.textDetail.OverwriteTextDetailUseCase
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TextDetailViewModelTest {
    private val loadTextDetailUseCase = mockk<LoadTextDetailUseCase>(relaxed = true)
    private val createTextDetailUseCase = mockk<CreateTextDetailUseCase>(relaxed = true)
    private val overwriteTextDetailUseCase = mockk<OverwriteTextDetailUseCase>(relaxed = true)

    private lateinit var viewModel: TextDetailViewModel


    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel =
            TextDetailViewModel(
                loadTextDetailUseCase,
                createTextDetailUseCase,
                overwriteTextDetailUseCase
            )

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
        viewModel.pickFontSize(TextDetailViewModel.FontSize.NORMAL)
        Assert.assertEquals(
            TextDetailViewModel.FontSize.NORMAL,
            TestUtils.getValue(viewModel.selectedFontSize)
        )

        viewModel.pickFontSize(TextDetailViewModel.FontSize.LARGE)
        Assert.assertEquals(
            TextDetailViewModel.FontSize.LARGE,
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
}