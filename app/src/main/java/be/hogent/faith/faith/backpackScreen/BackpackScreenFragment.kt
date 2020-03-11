package be.hogent.faith.faith.backpackScreen

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
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
    private var menuPopupHelper : MenuPopupHelper? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
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
        initialiseMenu()
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun startListeners() {

        backpackViewModel.details.observe(this, Observer { details ->
            detailThumbnailsAdapter?.updateDetailsList(details)
        })

        backpackBinding.btnBackpackAdd.setOnClickListener {
            backpackViewModel.setOnAddClicked(it)
        }

        backpackViewModel.onAddClicked.observe(this, Observer {
            backpackViewModel.changePopupMenuState()
        })

        backpackBinding.btnBackpackDrawCancel.setOnClickListener {
            backpackViewModel.goToCityScreen()
        }

        backpackBinding.backpackMenuFilter.search_bar.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                backpackViewModel.filterSearchBar(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                backpackViewModel.filterSearchBar(query)
                return true
            }
        })

        backpackBinding.backpackMenuFilter.filterknop_teksten.setOnClickListener {
            backpackViewModel.setFilters(TEXT_DETAIL)
            setBtnState(it)
        }
        backpackBinding.backpackMenuFilter.filterknop_audio.setOnClickListener {
            backpackViewModel.setFilters(AUDIO_DETAIL)
            setBtnState(it)
        }
        backpackBinding.backpackMenuFilter.filterknop_foto.setOnClickListener {
            backpackViewModel.setFilters(PICTURE_DETAIL)
            setBtnState(it)
        }
        backpackBinding.backpackMenuFilter.filterknop_tekeningen.setOnClickListener {
            backpackViewModel.setFilters(DRAW_DETAIL)
            setBtnState(it)
        }

        backpackViewModel.isDetailScreenOpen.observe(this, Observer {
            if (!it) { closeMenu() }
        })

        backpackBinding.btnBackpackDelete.setOnClickListener {
            backpackViewModel.setIsInEditMode()
        }

        backpackViewModel.isInEditMode.observe(this, Observer{
            if(backpackViewModel.isInEditMode.value == OpenState.OPEN){
                detailThumbnailsAdapter!!.hide(false)
                backpackViewModel.viewButtons(false)
            }
            else{
            detailThumbnailsAdapter!!.hide(true)
                backpackViewModel.viewButtons(true)
        }
        })

        backpackViewModel.isPopupMenuOpen.observe(this, Observer {
            if(it == OpenState.OPEN){
                openMenu()
                backpackBinding.btnBackpackAdd.background = resources.getDrawable(R.drawable.ic_add_btn_selected, null)
            }
            else if(it == OpenState.CLOSED){
                closeMenu()
                backpackBinding.btnBackpackAdd.background = resources.getDrawable(R.drawable.add_btn, null)
            }
        })

        backpackViewModel.initialize()
    }

    private fun setBtnState(it: View) {
        if(it.backgroundTintList == ColorStateList.valueOf(Color.GRAY)){
            it.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        }else{
            it.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
        }

    }

    private fun initialiseMenu(){
        addDetailMenu = PopupMenu(backpackBinding.btnBackpackAdd.context, backpackBinding.btnBackpackAdd, Gravity.END, 0, R.style.PopupMenu_AddDetail)

        addDetailMenu.menuInflater.inflate(R.menu.menu_backpack, addDetailMenu.menu)

        menuPopupHelper = MenuPopupHelper(backpackBinding.btnBackpackAdd.context, addDetailMenu.menu as MenuBuilder, backpackBinding.btnBackpackAdd)

        menuPopupHelper!!.setOnDismissListener {
            menuPopupHelper!!.dismiss()
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
            menuPopupHelper!!.dismiss()
            true
        }

        menuPopupHelper!!.setForceShowIcon(true)
    }

    private fun closeMenu(){
        menuPopupHelper?.dismiss()
    }

    private fun openMenu() {
        menuPopupHelper!!.show()
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