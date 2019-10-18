package be.hogent.faith.faith.emotionCapture.drawing.makeDrawing

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentDrawTabsBinding
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.drawing.DrawFragment
import be.hogent.faith.faith.emotionCapture.drawing.DrawViewModel
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import be.hogent.faith.util.TAG
import com.divyanshu.draw.widget.DrawView
import com.google.android.material.tabs.TabLayout
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

    private lateinit var drawBinding: FragmentDrawTabsBinding

    private var navigation: DrawingScreenNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        drawBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_draw_tabs, container, false)
        drawBinding.drawViewModel = drawViewModel
        drawBinding.lifecycleOwner = this

        return drawBinding.root
    }

    override fun onStart() {
        super.onStart()

        configureTemplatesRecyclerView()
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
        drawViewModel.eraserClicked.observe(this, Observer {
            drawView.setColor(Color.WHITE)
        })

        drawViewModel.saveClicked.observe(this, Observer {
            // TODO: move to something async, maybe a coroutine?
            drawView.getBitmap { bitmap ->
                eventViewModel.saveDrawing(bitmap)
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

    private fun configureTemplatesRecyclerView() {
        setUpTemplatesRecyclerView()

        drawBinding.tabsDrawingTemplates.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab) {
                    //NOP
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    // NOP
                }

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
        val imagesAdapter = ImagesAdapter(createListOfImageResources(imageResArray))
        drawBinding.recyclerViewDrawingTemplates.apply {
            adapter = imagesAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
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
