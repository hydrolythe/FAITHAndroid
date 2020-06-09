package be.hogent.faith.faith.treasureChest

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailThumbnailsAdapter
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class TreasureChestFragment : Fragment() {

    private var navigation: TreasureChestDetailsNavigationListener? = null
    private val treasureChestViewModel: TreasureChestViewModel by viewModel()
    private lateinit var treasureChestBinding: be.hogent.faith.databinding.FragmentTreasurechestBinding
    private var detailThumbnailsAdapter: DetailThumbnailsAdapter? = null
    private lateinit var addDetailMenu: PopupMenu
    private var menuIsOpen: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        treasureChestBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_treasurechest, container, false)

        treasureChestBinding.treasureChestViewModel = treasureChestViewModel
        treasureChestBinding.lifecycleOwner = this@TreasureChestFragment

        return treasureChestBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TreasureChestDetailsNavigationListener) {
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
            requireNotNull(activity) as TreasureChestActivity
        )
        treasureChestBinding.rvTreasurechest.layoutManager = GridLayoutManager(activity, 5)
        treasureChestBinding.rvTreasurechest.adapter = detailThumbnailsAdapter
    }

    override fun onStop() {
        super.onStop()
        detailThumbnailsAdapter = null
    }

    private fun startListeners() {
        treasureChestViewModel.filteredDetails.observe(this, Observer { details ->
            detailThumbnailsAdapter?.submitList(details)
        })

        treasureChestViewModel.deleteModeEnabled.observe(this, Observer { enabled ->
            detailThumbnailsAdapter!!.setItemsAsDeletable(enabled)
        })

        treasureChestViewModel.addButtonClicked.observe(viewLifecycleOwner, Observer {
            menuIsOpen = menuIsOpen.not()
            if (menuIsOpen) {
                treasureChestBinding.btnTreasurechestAdd.setImageResource(R.drawable.ic_knop_sluit_opties)
            } else {
                treasureChestBinding.btnTreasurechestAdd.setImageResource(R.drawable.add_btn)
            }
            addDetailMenu.show()
        })

        treasureChestViewModel.errorMessage.observe(this, Observer { message ->
            Toast.makeText(context, resources.getString(message), Toast.LENGTH_LONG).show()
        })
    }

    private fun initialiseMenu() {
        addDetailMenu = PopupMenu(
            treasureChestBinding.btnTreasurechestAdd.context,
            treasureChestBinding.btnTreasurechestAdd,
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
        addDetailMenu.setOnDismissListener {
            menuIsOpen = menuIsOpen.not()
            if (menuIsOpen) {
                treasureChestBinding.btnTreasurechestAdd.setImageResource(R.drawable.ic_knop_sluit_opties)
            } else {
                treasureChestBinding.btnTreasurechestAdd.setImageResource(R.drawable.add_btn)
            }
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

    private fun setDrawable(enabled: Boolean, button: ImageButton, image: Int, imageSelected: Int) {
        button.setImageDrawable(
            AppCompatResources.getDrawable(
                this.requireContext(),
                if (enabled) imageSelected else image
            )
        )
    }

    interface TreasureChestDetailsNavigationListener {
        fun startPhotoDetailFragment()
        fun startAudioDetailFragment()
        fun startDrawingDetailFragment()
        fun startTextDetailFragment()
        fun startVideoDetailFragment()
        fun startExternalFileDetailFragment()

        fun openDetailScreenFor(detail: Detail)

        fun closeTreasureChest()
    }

    companion object {
        fun newInstance(): TreasureChestFragment {
            return TreasureChestFragment()
        }
    }
}