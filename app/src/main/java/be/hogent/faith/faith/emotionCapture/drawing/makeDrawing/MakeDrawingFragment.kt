package be.hogent.faith.faith.emotionCapture.drawing.makeDrawing

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.drawing.DrawFragment
import be.hogent.faith.faith.emotionCapture.drawing.DrawViewModel
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import be.hogent.faith.util.TAG
import com.divyanshu.draw.widget.DrawView
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.error.ScopeNotCreatedException
import org.koin.core.qualifier.named

class MakeDrawingFragment : DrawFragment() {

    override val drawViewModel: DrawViewModel
        get() {
            Log.d(TAG, "Trying to get Drawing scope in MakeDrawing")
            val scope = try {
                getKoin().getScope(KoinModules.DRAWING_SCOPE_ID)
            } catch (e: ScopeNotCreatedException) {
                Log.d(TAG, "No Drawing scope available - Creating Drawing scope in MakeDrawing")
                getKoin().createScope(
                    KoinModules.DRAWING_SCOPE_ID,
                    named(KoinModules.DRAWING_SCOPE_NAME)
                )
            }
            return scope.get()
        }

    private val eventViewModel: EventViewModel by sharedViewModel()

    override val drawView: DrawView
        get() = drawBinding.drawView

    private val premadeImagesProvider by inject<PremadeImagesProvider>()

    private lateinit var drawBinding: FragmentDrawBinding

    private var navigation: DrawingScreenNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DrawingScreenNavigation) {
            navigation = context
        }
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
            navigation?.backToEvent()
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

    interface DrawingScreenNavigation {
        fun backToEvent()
    }
}
