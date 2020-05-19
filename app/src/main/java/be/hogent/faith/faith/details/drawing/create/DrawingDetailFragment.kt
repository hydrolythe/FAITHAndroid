package be.hogent.faith.faith.details.drawing.create

import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentDrawBinding
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.DetailFragment
import be.hogent.faith.faith.details.DetailsFactory
import be.hogent.faith.faith.details.drawing.create.draggableImages.DragListener
import be.hogent.faith.faith.details.drawing.create.draggableImages.ImagesAdapter
import be.hogent.faith.faith.di.KoinModules
import com.divyanshu.draw.widget.DrawView
import com.google.android.material.tabs.TabLayout
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import org.koin.android.ext.android.getKoin
import org.koin.core.error.ScopeNotCreatedException
import org.koin.core.qualifier.named
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import kotlin.reflect.KClass

private const val DRAWING_DETAIL = "uuid of the DrawingDetail"

class DrawingDetailFragment : DrawFragment(),
    DetailFragment<DrawingDetail> {
    override val drawViewModel: DrawViewModel
        get() = drawingDetailViewModel

    private lateinit var drawingDetailViewModel: DrawingDetailViewModel

    override val drawView: DrawView
        get() = drawBinding.drawView

    override fun saveBitmap() {
        drawView.getBitmap { bitmap ->
            drawingDetailViewModel.onBitMapAvailable(bitmap)
        }
    }

    private lateinit var drawBinding: FragmentDrawBinding

    override lateinit var detailFinishedListener: DetailFinishedListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        drawBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_draw, container, false)
        drawBinding.drawViewModel = drawingDetailViewModel
        drawBinding.lifecycleOwner = this

        if (existingDrawingGiven()) {
            loadExistingDrawing()
        }

        return drawBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
    }

    private fun setUpViewModel() {
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
        drawingDetailViewModel = scope.get()
    }

    override fun onStart() {
        super.onStart()

        configureTemplatesRecyclerView()
        configureDrawView()

        startListeners()
    }

    private fun loadExistingDrawing() {
        val existingDetail = requireArguments().getSerializable(DRAWING_DETAIL) as DrawingDetail
        drawView.setImageBackground(existingDetail.file)
        drawingDetailViewModel.loadExistingDetail(existingDetail)
    }

    private fun existingDrawingGiven(): Boolean {
        return arguments?.getSerializable(DRAWING_DETAIL) != null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DetailFinishedListener) {
            detailFinishedListener = context
        } else {
            throw AssertionError("A detailFragment has to be started with a DetailFinishedListener")
        }
    }

    private fun startListeners() {
        drawViewModel.customColorClicked.observe(this, Observer {
            val builder = ColorPickerDialog.Builder(this.context)
                .setTitle("Kies een kleur")
                .attachAlphaSlideBar(false)
                .attachBrightnessSlideBar(true)

                .setPositiveButton(
                    getString(R.string.confirm),
                    ColorEnvelopeListener { envelope, _ ->
                        if (envelope != null)
                            drawViewModel.setCustomColor(envelope.color)
                    })
                .setNegativeButton(
                    getString(R.string.cancel)
                ) { dialogInterface, _ -> dialogInterface.dismiss() }
            builder.show()
        })

        drawingDetailViewModel.textClicked.observe(this, Observer {
            drawView.pickTextTool()
        })

        drawingDetailViewModel.savedDetail.observe(this, Observer { detail ->
            if (detail == null) {
                return@Observer
            }
            detailFinishedListener.onDetailFinished(detail)

            // Close the scope so when we start a new drawing we start with an empty canvas.
            kotlin.runCatching { getKoin().getScope(KoinModules.DRAWING_SCOPE_ID) }.onSuccess {
                it.close()
            }

            navigation?.backToEvent()
        })

        drawingDetailViewModel.getDetailMetaData.observe(this, Observer {
            val saveDialog = DetailsFactory.createMetaDataDialog(
                requireActivity()::class as KClass<FragmentActivity>,
                DrawingDetail::class as KClass<Detail>
            )
            if (saveDialog == null)
                drawingDetailViewModel.setDetailsMetaData()
            else {
                saveDialog.setTargetFragment(this, 22)
                saveDialog.show(getParentFragmentManager(), null)
            }
        })
    }

    override fun onFinishSaveDetailsMetaData(title: String, dateTime: LocalDateTime) {
        drawingDetailViewModel.setDetailsMetaData(title, dateTime)
    }

    private fun configureDrawView() {
        with(drawView) {
            setOnDragListener(DragListener(this))
        }
    }

    private fun configureTemplatesRecyclerView() {
        setUpTemplatesRecyclerView()

        drawBinding.tabsDrawingTemplates.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab) {}
                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabSelected(tab: TabLayout.Tab) {
                    val imagesAdapter =
                        drawBinding.recyclerViewDrawingTemplates.adapter as ImagesAdapter
                    val array = when (tab.position) {
                        0 -> R.array.templates_people
                        1 -> R.array.templates_attributes
                        2 -> R.array.templates_decor
                        3 -> R.array.templates_metaphors
                        else -> throw IllegalArgumentException("Unknown templates specifier passed")
                    }
                    val imageResArray = requireContext().resources.obtainTypedArray(array)
                    val imageResList = createListOfImageResources(imageResArray)
                    imageResArray.recycle()
                    imagesAdapter.setNewImages(imageResList)
                }
            })
    }

    private fun createListOfImageResources(imageResArray: TypedArray): MutableList<Int> {
        val imageResList = mutableListOf<Int>()
        for (i in 0 until imageResArray.length()) {
            imageResList.add(imageResArray.getResourceId(i, -1))
        }
        return imageResList
    }

    private fun setUpTemplatesRecyclerView() {
        val imageResArray = requireContext().resources.obtainTypedArray(R.array.templates_people)
        val imagesAdapter =
            ImagesAdapter(
                createListOfImageResources(imageResArray)
            )
        drawBinding.recyclerViewDrawingTemplates.apply {
            adapter = imagesAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    companion object {
        fun newInstance(): DrawingDetailFragment {
            return DrawingDetailFragment()
        }

        fun newInstance(detail: DrawingDetail): DrawingDetailFragment {
            val args = Bundle()
            args.putSerializable(DRAWING_DETAIL, detail)

            return newInstance().apply { arguments = args }
        }
    }
}
