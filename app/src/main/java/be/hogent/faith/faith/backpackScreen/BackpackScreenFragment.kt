package be.hogent.faith.faith.backpackScreen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.SearchView
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailThumbnailsAdapter
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class BackpackScreenFragment : Fragment() {

    private var navigation: BackpackDetailsNavigationListener? = null
    private val backpackViewModel: BackpackViewModel by sharedViewModel()
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

        backpackViewModel.filteredDetails.observe(this, Observer { details ->
            detailThumbnailsAdapter?.submitList(details)
        })

        backpackBinding.backpackMenuFilter.searchBar.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                backpackViewModel.setSearchStringText(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                backpackViewModel.setSearchStringText(query)
                return true
            }
        })

        backpackViewModel.deleteEnabled.observe(this, Observer { enabled ->
            if (enabled) {
                detailThumbnailsAdapter!!.setItemsAsDeletable(true)
                backpackViewModel.viewButtons(false)
            } else {
                detailThumbnailsAdapter!!.setItemsAsDeletable(false)
                backpackViewModel.viewButtons(true)
            }
        })
        backpackBinding.btnBackpackAdd.setOnClickListener {
            addDetailMenu.show()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun initialiseMenu() {
        addDetailMenu = PopupMenu(
            backpackBinding.btnBackpackAdd.context,
            backpackBinding.btnBackpackAdd,
            Gravity.END,
            0,
            R.style.PopupMenu_AddDetail
        )

        addDetailMenu.menuInflater.inflate(R.menu.menu_backpack, addDetailMenu.menu)

        addDetailMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.backpack_menu_addAudio ->
                    navigation?.startAudioDetailFragment()
                R.id.backpack_menu_addDrawing ->
                    navigation?.startDrawingDetailFragment()
                R.id.backpack_menu_addFile ->
                    navigation?.startExternalFileDetailFragment()
                R.id.backpack_menu_addFoto ->
                    navigation?.startPhotoDetailFragment()
                R.id.backpack_menu_addText ->
                    navigation?.startTextDetailFragment()
                R.id.backpack_menu_addVideo ->
                    navigation?.startVideoDetailFragment()
            }
            true
        }
        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(addDetailMenu)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
        } catch (e: Exception) {
            Timber.e("Error showing icons")
        }
    }

    interface BackpackDetailsNavigationListener {
        fun startPhotoDetailFragment()
        fun startAudioDetailFragment()
        fun startDrawingDetailFragment()
        fun startTextDetailFragment()
        fun startVideoDetailFragment()
        fun startExternalFileDetailFragment()

        fun openDetailScreenFor(detail: Detail)

        fun closeBackpack()
    }

    companion object {
        fun newInstance(): BackpackScreenFragment {
            return BackpackScreenFragment()
        }
    }
}