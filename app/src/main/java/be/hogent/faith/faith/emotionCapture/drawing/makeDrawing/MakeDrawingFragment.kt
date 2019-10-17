package be.hogent.faith.faith.emotionCapture.drawing.makeDrawing

import android.content.Context
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
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.drawing.DrawFragment
import be.hogent.faith.faith.emotionCapture.drawing.DrawViewModel
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import com.divyanshu.draw.widget.DrawView
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.error.ScopeNotCreatedException
import org.koin.core.qualifier.named
import timber.log.Timber
import java.util.UUID

private const val DRAWING_DETAIL_UUID = "uuid of the DrawingDetail"

class MakeDrawingFragment : DrawFragment() {
    override val drawViewModel: DrawViewModel
        get() {
            Timber.d("Trying to get Drawing scope in MakeDrawing")
            val scope = try {
                getKoin().getScope(KoinModules.DRAWING_SCOPE_ID)
            } catch (e: ScopeNotCreatedException) {
                Timber.d("No Drawing scope available - Creating Drawing scope in MakeDrawing")
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

        if (existingDrawingGiven()) {
            loadExistingDrawing()
        }

        startListeners()
    }

    private fun loadExistingDrawing() {
        val drawingDetail = getGivenDrawingDetail()

        drawView.setImageBackground(drawingDetail.file)

        drawViewModel.loadExistingDrawingDetail(drawingDetail)
    }

    private fun existingDrawingGiven(): Boolean {
        return arguments?.getSerializable(DRAWING_DETAIL_UUID) != null
    }

    private fun getGivenDrawingDetail(): DrawingDetail {
        val detailUuid = arguments!!.getSerializable(DRAWING_DETAIL_UUID) as UUID
        return eventViewModel.event.value!!.getDetail(detailUuid) as DrawingDetail
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DrawingScreenNavigation) {
            navigation = context
        }
    }

    private fun startListeners() {
        drawViewModel.textClicked.observe(this, Observer {
            drawView.pickTextTool()
        })
        drawViewModel.eraserClicked.observe(this, Observer {
            drawView.pickEraserTool()
        })
        drawViewModel.pencilClicked.observe(this, Observer {
            drawView.pickDrawingTool()
        })

        drawViewModel.saveClicked.observe(this, Observer {
            // TODO: move to something async, maybe a coroutine?
            drawView.getBitmap { bitmap ->
                eventViewModel.saveDrawing(bitmap, drawViewModel.existingDetail.value)
            }
        })
        eventViewModel.drawingSavedSuccessFully.observe(this, Observer {
            Toast.makeText(context, R.string.save_drawing_success, Toast.LENGTH_SHORT).show()

            // Close the scope so when we start a new drawing we start with an empty canvas.
            kotlin.runCatching { getKoin().getScope(KoinModules.DRAWING_SCOPE_ID) }.onSuccess {
                it.close()
            }

            navigation?.backToEvent()
        })
    }

    private fun configureDrawView() {
        with(drawView) {
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

        fun newInstance(detailUuid: UUID): MakeDrawingFragment {
            val args = Bundle().apply {
                putSerializable(DRAWING_DETAIL_UUID, detailUuid)
            }
            return newInstance().apply {
                arguments = args
            }
        }
    }

    interface DrawingScreenNavigation {
        fun backToEvent()
    }
}
