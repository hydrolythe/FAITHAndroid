package be.hogent.faith.faith.emotionCapture.drawing

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.divyanshu.draw.widget.DrawView
import org.koin.android.viewmodel.ext.android.sharedViewModel

abstract class DrawFragment : Fragment() {
    protected val drawViewModel: DrawViewModel by sharedViewModel()

    abstract val drawView: DrawView

    override fun onStart() {
        super.onStart()

        setUpListeners()
    }

    private fun setUpListeners() {
        drawViewModel.selectedColor.observe(this, Observer { newColor ->
            drawView.setColor(newColor)
        })
        drawViewModel.selectedLineWidth.observe(this, Observer { lineWidth ->
            drawView.setStrokeWidth(lineWidth.width)
        })
        drawViewModel.undoClicked.observe(this, Observer {
            drawView.undo()
        })
    }
}