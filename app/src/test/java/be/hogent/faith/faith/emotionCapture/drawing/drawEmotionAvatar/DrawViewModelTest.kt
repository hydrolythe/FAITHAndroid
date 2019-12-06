package be.hogent.faith.faith.emotionCapture.drawing.drawEmotionAvatar

import android.graphics.Color
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import be.hogent.faith.faith.TestUtils.getValue
import be.hogent.faith.faith.emotionCapture.drawing.DrawViewModel
import be.hogent.faith.faith.emotionCapture.drawing.DrawViewModel.LineWidth
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DrawViewModelTest {

    private lateinit var viewModel: DrawViewModel

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = DrawViewModel()
    }

    @Test
    fun drawEmotionVM_pickNewColor_isSelected() {
        viewModel.pickColor(Color.BLACK)
        assertEquals(Color.BLACK, getValue(viewModel.selectedColor))

        viewModel.pickColor(Color.GREEN)
        assertEquals(Color.GREEN, getValue(viewModel.selectedColor))
    }

    @Test
    fun drawEmotionVM_selectLineWidth_isSelected() {
        viewModel.setLineWidth(LineWidth.MEDIUM)
        assertEquals(LineWidth.MEDIUM, getValue(viewModel.selectedLineWidth))

        viewModel.setLineWidth(LineWidth.THIN)
        assertEquals(LineWidth.THIN, getValue(viewModel.selectedLineWidth))
    }

    @Test
    fun drawEmotionVM_pickTextOrPencil_visibilityDrawTextToolsIsSet() {
        viewModel.onTextClicked()
        assertEquals(View.GONE, getValue(viewModel.visibilityDrawTools))
        assertEquals(View.VISIBLE, getValue(viewModel.visibilityTextTools))

        viewModel.onPencilClicked()
        assertEquals(View.VISIBLE, getValue(viewModel.visibilityDrawTools))
        assertEquals(View.GONE, getValue(viewModel.visibilityTextTools))

        viewModel.onEraserClicked()
        assertEquals(View.VISIBLE, getValue(viewModel.visibilityDrawTools))
        assertEquals(View.GONE, getValue(viewModel.visibilityTextTools))
    }
}