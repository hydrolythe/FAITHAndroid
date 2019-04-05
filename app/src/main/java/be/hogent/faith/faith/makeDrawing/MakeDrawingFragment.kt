package be.hogent.faith.faith.makeDrawing

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentDrawBinding
import be.hogent.faith.faith.enterEventDetails.EventDetailsViewModel
import be.hogent.faith.util.TAG
import org.koin.android.viewmodel.ext.android.sharedViewModel

class MakeDrawingFragment : Fragment() {

    private val drawViewModel: DrawViewModel by sharedViewModel()
    private val eventDetailsViewModel: EventDetailsViewModel by sharedViewModel()

    private lateinit var drawBinding: FragmentDrawBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        drawBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_draw, container, false)
        drawBinding.drawViewModel = drawViewModel
        drawBinding.lifecycleOwner = this
        return drawBinding.root
    }

    override fun onStart() {
        super.onStart()

        setUpRecyclerView()
        setUpDrawView()
        setUpListeners()
    }

    private fun setUpListeners() {
        drawViewModel.selectedColor.observe(this, Observer { newColor ->
            Log.i(TAG, "Color set to $newColor")
            drawBinding.drawCanvas.setColor(newColor)
        })
        drawViewModel.selectedLineWidth.observe(this, Observer { lineWidth ->
            Log.i(TAG, "Line width set to $lineWidth")
            drawBinding.drawCanvas.setStrokeWidth(lineWidth.width)
        })
        drawViewModel.undoClicked.observe(this, Observer {
            Log.i(TAG, "Last action undone")
            drawBinding.drawCanvas.undo()
        })
    }

    private fun setUpDrawView() {
        with(drawBinding.drawCanvas) {
            setOnDragListener(DragListener(this))
        }
    }

    private fun setUpRecyclerView() {
        drawBinding.containerDrawImages.recyclerViewDrawingImages.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = ImagesAdapter(
                context, listOf(
                    R.drawable.image_auto,
                    R.drawable.image_auto,
                    R.drawable.image_auto,
                    R.drawable.image_mannetje,
                    R.drawable.image_auto,
                    R.drawable.image_mannetje,
                    R.drawable.image_auto,
                    R.drawable.image_mannetje,
                    R.drawable.image_mannetje,
                    R.drawable.image_mannetje,
                    R.drawable.image_auto,
                    R.drawable.image_mannetje,
                    R.drawable.image_auto,
                    R.drawable.image_mannetje,
                    R.drawable.image_mannetje
                )
            )
        }
    }
}
