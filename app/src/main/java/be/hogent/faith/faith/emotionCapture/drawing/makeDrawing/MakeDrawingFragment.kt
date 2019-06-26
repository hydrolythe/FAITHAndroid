package be.hogent.faith.faith.emotionCapture.drawing.makeDrawing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentDrawBinding
import be.hogent.faith.faith.emotionCapture.drawing.DrawFragment
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import com.divyanshu.draw.widget.DrawView
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

class MakeDrawingFragment : DrawFragment() {

    private val eventViewModel: EventViewModel by sharedViewModel()

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

        startListeners()
    }

    private fun startListeners() {
        drawViewModel.saveClicked.observe(this, Observer {
            // TODO: move to something async, maybe a coroutine?
            drawBinding.drawView.getBitmap { bitmap ->
                eventViewModel.saveDrawing(bitmap)
            }
        })
        eventViewModel.drawingSavedSuccessFully.observe(this, Observer {
            Toast.makeText(context, R.string.save_drawing_success, Toast.LENGTH_SHORT).show()
        })
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
            adapter = ImagesAdapter(premadeImagesProvider.provideImages())
        }
    }

    companion object {
        fun newInstance(): MakeDrawingFragment {
            return MakeDrawingFragment()
        }
    }
}
