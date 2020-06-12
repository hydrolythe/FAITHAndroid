package be.hogent.faith.faith.detailscontainer

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailThumbnailsAdapter
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder
import timber.log.Timber

abstract class DetailsContainerFragment<T : DetailsContainer> : Fragment() {

    private var navigation: DetailsContainerNavigationListener? = null
    private var detailThumbnailsAdapter: DetailThumbnailsAdapter? = null
    private lateinit var addDetailMenu: PopupMenu
    protected var menuIsOpen: Boolean = false
    protected abstract val detailsContainerViewModel: DetailsContainerViewModel<T>

    protected lateinit var binding: ViewDataBinding

    protected abstract val addButton: ImageButton
    protected abstract val deleteButton: ImageButton
    protected abstract val detailsRecyclerView: RecyclerView

    protected abstract val layoutResourceID: Int
    protected abstract val menuResourceID: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, layoutResourceID, container, false)

        binding.lifecycleOwner = this

        setViewModel()

        return binding.root
    }

    /**
     * Sets the viewmodel into the binding
     */
    abstract fun setViewModel()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DetailsContainerNavigationListener) {
            navigation = context
        }
    }

    override fun onStart() {
        super.onStart()
        startListeners()
        updateUI()
        initAddDetailMenu()
    }

    private fun initAddDetailMenu() {
        addDetailMenu =
            PopupMenu(addButton.context, addButton, Gravity.END, 0, R.style.PopupMenu_AddDetail)

        addDetailMenu.menuInflater.inflate(menuResourceID, addDetailMenu.menu)

        addDetailMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.container_menu_addAudio ->
                    navigation?.startAudioDetailFragment()
                R.id.container_menu_addDrawing ->
                    navigation?.startDrawingDetailFragment()
                R.id.container_menu_addFile ->
                    navigation?.startExternalFileDetailFragment()
                R.id.container_menu_addPhoto ->
                    navigation?.startPhotoDetailFragment()
                R.id.container_menu_addText ->
                    navigation?.startTextDetailFragment()
                R.id.container_menu_addVideo ->
                    navigation?.startYoutubeDetailFragment()
            }
            true
        }
        addDetailMenu.setOnDismissListener {
            menuIsOpen = menuIsOpen.not()
            if (menuIsOpen) {
                addButton.setImageResource(R.drawable.ic_knop_sluit_opties)
            } else {
                addButton.setImageResource(R.drawable.add_btn)
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

    private fun updateUI() {
        detailThumbnailsAdapter = DetailThumbnailsAdapter(
            requireNotNull(activity) as DetailViewHolder.ExistingDetailNavigationListener
        )
        detailsRecyclerView.layoutManager = GridLayoutManager(activity, 6)
        detailsRecyclerView.adapter = detailThumbnailsAdapter
    }

    private fun startListeners() {
        detailsContainerViewModel.addButtonClicked.observe(this, Observer {
            menuIsOpen = menuIsOpen.not()
            if (menuIsOpen) {
                addButton.setImageResource(R.drawable.ic_knop_sluit_opties)
            } else {
                addButton.setImageResource(R.drawable.add_btn)
            }
            addDetailMenu.show()
        })

        detailsContainerViewModel.filteredDetails.observe(this, Observer { details ->
            detailThumbnailsAdapter?.submitList(details)
            if (details.isEmpty()) {
                detailsContainerViewModel.deleteModeEnabled.postValue(false)
            } else {
                deleteButton.isClickable = true
            }
        })

        detailsContainerViewModel.deleteModeEnabled.observe(this, Observer { enabled ->
            detailThumbnailsAdapter!!.setItemsAsDeletable(enabled)
        })

        detailsContainerViewModel.goToCityScreen.observe(this, Observer {
            navigation!!.closeScreen()
        })

        detailsContainerViewModel.errorMessage.observe(this, Observer { message ->
            Toast.makeText(context, resources.getString(message), Toast.LENGTH_LONG).show()
        })
    }

    interface DetailsContainerNavigationListener {
        fun startPhotoDetailFragment()
        fun startAudioDetailFragment()
        fun startDrawingDetailFragment()
        fun startTextDetailFragment()
        fun startYoutubeDetailFragment()
        fun startExternalFileDetailFragment()

        fun openDetailScreenFor(detail: Detail)

        fun closeScreen()
    }
}
