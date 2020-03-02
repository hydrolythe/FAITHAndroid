package be.hogent.faith.faith.backpackScreen

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailThumbnailsAdapter
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class BackpackScreenFragment : Fragment() {

    private var navigation: BackpackDetailsNavigationListener? = null

    private val backpackViewModel: BackpackViewModel by sharedViewModel()
 //   private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    private lateinit var backpackBinding: be.hogent.faith.databinding.FragmentBackpackBinding

    private var detailThumbnailsAdapter: DetailThumbnailsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        backpackBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_backpack, container, false)

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
    }

    private fun updateUI() {

        backpackBinding.recyclerviewBackpack.apply {
            setHasFixedSize(true)
            layoutManager =  GridLayoutManager(activity,5)
            // Start with empty list and then fill it in

            adapter = DetailThumbnailsAdapter(
                emptyList(),
                requireNotNull(activity) as BackpackScreenActivity
            )
        }

        detailThumbnailsAdapter =
            backpackBinding.recyclerviewBackpack.adapter as DetailThumbnailsAdapter
        // determineRVVisibility()
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

        backpackBinding.btnAdd.setOnClickListener {
            onAddClicked()
            rotateBtnForward(backpackBinding.btnAdd)
        }
    }

    private fun rotateBtnForward(view: View) {
        ViewCompat.animate(view)
            .rotation(135.0F)
            .withLayer()
            .setDuration(300L)
            .setInterpolator(OvershootInterpolator(10.0F))
            .start()
    }

    private fun rotateBtnBackwards(view: View) {
        ViewCompat.animate(view)
            .rotation(0.0F)
            .withLayer()
            .setDuration(300L)
            .setInterpolator(OvershootInterpolator(10.0F))
            .start()
    }

    private fun onAddClicked() {
        val popUpMenu = PopupMenu(activity, backpackBinding.btnAdd)
        popUpMenu.menuInflater.inflate(R.menu.menu_backpack, popUpMenu.menu)

        popUpMenu.setOnMenuItemClickListener { item ->
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
        popUpMenu.show()
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












