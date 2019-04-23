package be.hogent.faith.faith.drawing.makeDrawing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentDrawBinding
import be.hogent.faith.faith.drawing.DrawFragment
import com.divyanshu.draw.widget.DrawView
import org.koin.android.ext.android.inject

class MakeDrawingFragment : DrawFragment() {

    override val drawView: DrawView
        get() = drawBinding.drawView

    private val premadeImagesProvider by inject<PremadeImagesProvider>()

    private lateinit var drawBinding: FragmentDrawBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        drawBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_draw, container, false)
        drawBinding.drawViewModel = drawViewModel
        drawBinding.lifecycleOwner = this

        return drawBinding.root
    }

    override fun onStart() {
        super.onStart()

        configureDraggableImagesRecyclerView()
        configureDrawView()
    }

    private fun configureDrawView() {
        with(drawBinding.drawView) {
            setOnDragListener(DragListener(this))
        }
    }

    private fun configureDraggableImagesRecyclerView() {
        drawBinding.containerDrawImages.recyclerViewDrawingImages.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = ImagesAdapter(
                context, premadeImagesProvider.provideImages()
            )
        }
    }

    companion object {
        fun newInstance(): MakeDrawingFragment {
            return MakeDrawingFragment()
        }
    }
}
