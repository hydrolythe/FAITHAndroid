package be.hogent.faith.faith.details.drawing.create

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.faith.backpackScreen.BackpackScreenActivity
import be.hogent.faith.faith.cinema.CinemaActivity
import com.divyanshu.draw.widget.DrawView
import kotlinx.android.synthetic.main.panel_brush_sizes.btn_draw_setMediumLineWidth
import kotlinx.android.synthetic.main.panel_brush_sizes.btn_draw_setThickLineWidth
import kotlinx.android.synthetic.main.panel_brush_sizes.btn_draw_setThinLineWidth
import kotlinx.android.synthetic.main.panel_drawing_colors.img_draw_blackSelected
import kotlinx.android.synthetic.main.panel_drawing_colors.img_draw_blueSelected
import kotlinx.android.synthetic.main.panel_drawing_colors.img_draw_greenSelected
import kotlinx.android.synthetic.main.panel_drawing_colors.img_draw_redSelected
import kotlinx.android.synthetic.main.panel_drawing_colors.img_draw_yellowSelected

abstract class DrawFragment : Fragment() {
    abstract val drawViewModel: DrawViewModel

    abstract val drawView: DrawView

    open val colorAlpha: Int = 255 // no transparancy

    protected var navigation: DrawingScreenNavigation? = null

    abstract fun saveBitmap()

    override fun onStart() {
        super.onStart()

        setUpListeners()

        // anders wordt zwarte kleur niet geselecteerd en wordt de alpha value niet ingesteld
        drawViewModel.pickColor(ContextCompat.getColor(requireContext(), R.color.black))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is DrawingScreenNavigation) {
            navigation = context
        }
    }

    private fun setUpListeners() {
        drawViewModel.selectedColor.observe(this, Observer { newColor ->
            drawView.setColor(newColor)
            drawView.setAlpha(colorAlpha)
            setSelectedColor(newColor)
            setBrushSizeDrawables()
        })
        drawViewModel.pencilClicked.observe(this, Observer {
            drawView.pickDrawingTool()
        })
        drawViewModel.selectedLineWidth.observe(this, Observer { lineWidth ->
            drawView.setStrokeWidth(lineWidth.width)
            setBrushSizeDrawables()
        })
        drawViewModel.undoClicked.observe(this, Observer {
            drawView.undo()
        })
        drawViewModel.redoClicked.observe(this, Observer {
            drawView.redo()
        })
        drawViewModel.saveClicked.observe(this, Observer {
            saveBitmap()
        })
        drawViewModel.cancelClicked.observe(this, Observer {
            showExitAlert()
        })
        drawViewModel.eraserClicked.observe(this, Observer {
            drawView.pickEraserTool(colorAlpha)
        })
        drawViewModel.drawingActions.observe(this, Observer {
            // It's very important that the drawCanvas doesn't create its own paths but uses a paths object
            // that is saved in such a way that it survives configuration changes. See [DrawViewModel].
            drawView.setActions(it)
        })
    }

    private fun setSelectedColor(@ColorInt newColor: Int) {
        img_draw_blackSelected.visibility = if (newColor == ContextCompat.getColor(requireContext(), R.color.black)) View.VISIBLE else View.GONE
        img_draw_blueSelected.visibility = if (newColor == ContextCompat.getColor(requireContext(), R.color.blue)) View.VISIBLE else View.GONE
        img_draw_redSelected.visibility = if (newColor == ContextCompat.getColor(requireContext(), R.color.red)) View.VISIBLE else View.GONE
        img_draw_greenSelected.visibility = if (newColor == ContextCompat.getColor(requireContext(), R.color.green)) View.VISIBLE else View.GONE
        img_draw_yellowSelected.visibility = if (newColor == ContextCompat.getColor(requireContext(), R.color.yellow))View.VISIBLE else View.GONE
    }

    protected fun setBrushSizeDrawables() {
        btn_draw_setThickLineWidth.setImageDrawable(getBrushSizeDrawable(DrawViewModel.LineWidth.THICK))
        btn_draw_setMediumLineWidth.setImageDrawable(getBrushSizeDrawable(DrawViewModel.LineWidth.MEDIUM))
        btn_draw_setThinLineWidth.setImageDrawable(getBrushSizeDrawable(DrawViewModel.LineWidth.THIN))
    }

    private fun getBrushSizeDrawable(lineWidth: DrawViewModel.LineWidth): GradientDrawable {
        val color: Int = drawViewModel.selectedColor.value!!
        val gradientDrawable =
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.circle
            ) as GradientDrawable
        gradientDrawable.setColor(color)
        gradientDrawable.setStroke(
            2,
            if (drawViewModel.selectedLineWidth.value != lineWidth) color else Color.BLACK
        )
        return gradientDrawable
    }

    private fun showExitAlert() {
        val alertDialog: AlertDialog = this.run {
            val builder = AlertDialog.Builder(this.requireContext()).apply {
                when (requireActivity()) {
                    is BackpackScreenActivity -> setTitle(R.string.dialog_to_the_backpack)
                    is CinemaActivity -> setTitle(R.string.dialog_to_the_cinema_title)
                    else -> setTitle(R.string.dialog_to_the_event_title)
                }
                setMessage(R.string.dialog_to_the_event_message)
                setPositiveButton(R.string.ok) { _, _ ->
                    navigation!!.backToEvent()
                }
                setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
            }
            builder.create()
        }
        alertDialog.show()
    }

    interface DrawingScreenNavigation {
        fun backToEvent()
    }
}