package be.hogent.faith.faith.details.drawing.create

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.divyanshu.draw.widget.DrawView

abstract class DrawFragment : Fragment() {
    abstract val drawViewModel: DrawViewModel

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
        drawViewModel.drawingActions.observe(this, Observer {
            // It's very important that the drawCanvas doesn't create its own paths but uses a paths object
            // that is saved in such a way that it survives configuration changes. See [DrawViewModel].
            drawView.setActions(it)
        })
    }
}