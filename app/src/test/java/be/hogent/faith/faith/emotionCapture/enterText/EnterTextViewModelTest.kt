package be.hogent.faith.faith.emotionCapture.enterText

import android.graphics.Color
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.faith.TestUtils

import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EnterTextViewModelTest {
    private lateinit var viewModel: EnterTextViewModel
    private val text = "Hello world"

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = EnterTextViewModel()
        viewModel.textChanged(text)
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
        Assert.assertEquals(EnterTextViewModel.FontSize.NORMAL.size, TestUtils.getValue(viewModel.selectedFontSize))

        viewModel.pickFontSize(EnterTextViewModel.FontSize.LARGE)
        Assert.assertEquals(EnterTextViewModel.FontSize.LARGE.size, TestUtils.getValue(viewModel.selectedFontSize))
    }

    @Test
    fun enterTextVM_onBoldClicked_callsListeners() {
        val observer = mockk<Observer<Unit>>(relaxed = true)
        viewModel.boldClicked.observeForever(observer)

        viewModel.onBoldClicked()

        verify { observer.onChanged(any()) }
    }

    @Test
    fun enterTextVM_onItalicClicked_callsListeners() {
        val observer = mockk<Observer<Unit>>(relaxed = true)
        viewModel.italicClicked.observeForever(observer)

        viewModel.onItalicClicked()

        verify { observer.onChanged(any()) }
    }

    @Test
    fun enterTextVM_onUnderlineClicked_callsListeners() {
        val observer = mockk<Observer<Unit>>(relaxed = true)
        viewModel.underlineClicked.observeForever(observer)

        viewModel.onUnderlineClicked()

        verify { observer.onChanged(any()) }
    }
}