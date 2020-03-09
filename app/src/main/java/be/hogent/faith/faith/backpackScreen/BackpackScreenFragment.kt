package be.hogent.faith.faith.backpackScreen

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.backpackScreen.DetailTypes.AUDIO_DETAIL
import be.hogent.faith.faith.backpackScreen.DetailTypes.DRAW_DETAIL
import be.hogent.faith.faith.backpackScreen.DetailTypes.PICTURE_DETAIL
import be.hogent.faith.faith.backpackScreen.DetailTypes.TEXT_DETAIL
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailThumbnailsAdapter
import kotlinx.android.synthetic.main.backpack_menu_filter.view.filterknop_audio
import kotlinx.android.synthetic.main.backpack_menu_filter.view.filterknop_foto
import kotlinx.android.synthetic.main.backpack_menu_filter.view.filterknop_tekeningen
import kotlinx.android.synthetic.main.backpack_menu_filter.view.filterknop_teksten
import kotlinx.android.synthetic.main.backpack_menu_filter.view.search_bar
import org.koin.android.viewmodel.ext.android.sharedViewModel

object DetailTypes {
    const val AUDIO_DETAIL = 1
    const val TEXT_DETAIL = 2
    const val PICTURE_DETAIL = 3
    const val DRAW_DETAIL = 4
}

class BackpackScreenFragment : Fragment() {

    private var navigation: BackpackDetailsNavigationListener? = null

    private val backpackViewModel: BackpackViewModel by sharedViewModel()
    //   private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    private lateinit var backpackBinding: be.hogent.faith.databinding.FragmentBackpackBinding
    private var detailThumbnailsAdapter: DetailThumbnailsAdapter? = null
    private lateinit var addDetailMenu: PopupMenu

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        backpackBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_backpack, container, false)

        backpackViewModel.viewButtons(true)

        backpackBinding.backpackViewModel = backpackViewModel
        backpackBinding.lifecycleOwner = this@BackpackScreenFragment

        backpackViewModel.openAddDetailMenu.observe(this, Observer {
            onAddClicked(it)
        })

        return backpackBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BackpackDetailsNavigationListener) {
            navigation = context
        }
    }

    override fun onStart() {
        super.onStart()
        startListeners()
        updateUI()
    }

    private fun updateUI() {
        detailThumbnailsAdapter = DetailThumbnailsAdapter(
            emptyList(),
            requireNotNull(activity) as BackpackScreenActivity
        )
        backpackBinding.recyclerviewBackpack.layoutManager = GridLayoutManager(activity, 5)
        backpackBinding.recyclerviewBackpack.adapter = detailThumbnailsAdapter
    }

    override fun onStop() {
        super.onStop()
        detailThumbnailsAdapter = null
    }

    private fun startListeners() {
        // TODO Update adapter when backpack changes, change this to userviewmodel when working with real data

        backpackViewModel.details.observe(this, Observer { details ->
            detailThumbnailsAdapter?.updateDetailsList(details)
        })

        backpackBinding.btnBackpackAdd.setOnClickListener {
            backpackViewModel.openAddDetailMenu(it)
        }

        backpackBinding.btnBackpackDrawCancel.setOnClickListener {
            backpackViewModel.goToCityScreen()
        }

        backpackBinding.backpackMenuFilter.search_bar.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {

                val filteredDetails = backpackViewModel.filterSearchBar(newText)
                detailThumbnailsAdapter!!.updateDetailsList(filteredDetails)
                // detailThumbnailsAdapter!!.filterSearchBar(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                val filteredDetails = backpackViewModel.filterSearchBar(query)
                detailThumbnailsAdapter!!.updateDetailsList(filteredDetails)
                return true
            }
        })

        backpackBinding.backpackMenuFilter.filterknop_teksten.setOnClickListener {
            val filteredDetails = backpackViewModel.filterType(TEXT_DETAIL)
            detailThumbnailsAdapter!!.updateDetailsList(filteredDetails)
        }
        backpackBinding.backpackMenuFilter.filterknop_audio.setOnClickListener {
            val filteredDetails = backpackViewModel.filterType(AUDIO_DETAIL)
            detailThumbnailsAdapter!!.updateDetailsList(filteredDetails)
        }
        backpackBinding.backpackMenuFilter.filterknop_foto.setOnClickListener {
            val filteredDetails = backpackViewModel.filterType(PICTURE_DETAIL)
            detailThumbnailsAdapter!!.updateDetailsList(filteredDetails)
        }
        backpackBinding.backpackMenuFilter.filterknop_tekeningen.setOnClickListener {
            val filteredDetails = backpackViewModel.filterType(DRAW_DETAIL)
            detailThumbnailsAdapter!!.updateDetailsList(filteredDetails)
        }

        backpackViewModel.isDetailScreenOpen.observe(this, Observer {
            if (!it) {
                backpackBinding.btnBackpackAdd.background = resources.getDrawable(R.drawable.add_btn, null)
                addDetailMenu.dismiss()
                }
    })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun onAddClicked(view: View) {
        addDetailMenu = PopupMenu(view.context, view, Gravity.END, 0, R.style.PopupMenu_AddDetail)
        backpackBinding.btnBackpackAdd.background = resources.getDrawable(R.drawable.ic_add_btn_selected, null)
        addDetailMenu.menuInflater.inflate(R.menu.menu_backpack, addDetailMenu.menu)

        val menuPopupHelper = MenuPopupHelper(view.context, addDetailMenu.menu as MenuBuilder, backpackBinding.btnBackpackAdd)

        addDetailMenu.setOnDismissListener {
            backpackBinding.btnBackpackAdd.background = resources.getDrawable(R.drawable.add_btn, null)
        }

        addDetailMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.backpack_menu_addAudio ->
                    navigation?.startAudioDetailFragment()
                R.id.backpack_menu_addDrawing ->
                    navigation?.startDrawingDetailFragment()
                R.id.backpack_menu_addFile ->
                    navigation?.startFileDetailFragment()
                R.id.backpack_menu_addFoto ->
                    navigation?.startPhotoDetailFragment()
                R.id.backpack_menu_addText ->
                    navigation?.startTextDetailFragment()
                R.id.backpack_menu_addVideo ->
                    navigation?.startVideoDetailFragment()
            }
            true
        }
            menuPopupHelper.setForceShowIcon(true)
            menuPopupHelper.show()
    }

    interface BackpackDetailsNavigationListener {
        fun startPhotoDetailFragment()
        fun startAudioDetailFragment()
        fun startDrawingDetailFragment()
        fun startTextDetailFragment()
        fun startVideoDetailFragment()
        fun startFileDetailFragment()

        fun openDetailScreenFor(detail: Detail)

        fun closeBackpack()
    }

    companion object {
        fun newInstance(): BackpackScreenFragment {
            return BackpackScreenFragment()
        }
    }
}