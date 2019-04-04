package be.hogent.faith.faith.chooseAvatar.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.Avatar
import be.hogent.faith.util.TAG
import be.hogent.faith.faith.util.getRotation
import kotlinx.android.synthetic.main.fragment_avatar.avatar_rv_avatar
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A [Fragment] subclass which allows the user to choose an AvatarItem and a Backpack.
 *
 * Use the [AvatarFragment.newInstance] factory method to create an instance of this fragment.
 *
 */
const val SELECTION_ID = "avatarSelection"

class AvatarFragment : Fragment() {

    /**
     * ViewModel used for the avatarItems.
     */
    private val avatarViewModel: AvatarViewModel by viewModel()

    /**
     * Object used to track the selection on the Recyclerview
     */
    private var avatarTracker: SelectionTracker<Long>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            // Of the Fragment is still in memory restore the state
            avatarTracker?.onRestoreInstanceState(savedInstanceState)
        }
    }

    override fun onStart() {
        super.onStart()
        registerAdapters()

        avatarViewModel.nextButtonClicked.observe(this, Observer<Any> {
            generateNewUser()
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: be.hogent.faith.databinding.FragmentAvatarBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_avatar, container, false)
        binding.avatarViewModel = avatarViewModel
        return binding.root
    }

    /**
     * Changes the orientation of the recyclerviews, depending on the orientation of the device;
     */
    private fun setOrientation() {
        // Check here so we can use FragmentScenario to test
        val orientation = if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).getRotation()
        } else {
            R.integer.LANDSCAPE
        }
        when (orientation) {
            R.integer.PORTRAIT -> {
                avatar_rv_avatar.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                avatar_rv_avatar.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
            R.integer.LANDSCAPE -> {
                avatar_rv_avatar.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
                avatar_rv_avatar.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
            }
        }
    }

    /**
     * Registers the adapters for the RecyclerViews.
     * Checks for the orientation of the device and sets the LayoutManager for the Adapter based on this.
     */
    private fun registerAdapters() {
        val avatarAdapter = AvatarItemAdapter()
        avatar_rv_avatar.adapter = avatarAdapter
        LinearSnapHelper().attachToRecyclerView(avatar_rv_avatar)
        setOrientation()
        avatarTracker = SelectionTracker.Builder<Long>(
            SELECTION_ID,
            avatar_rv_avatar,
            AvatarItemAdapter.KeyProvider(avatar_rv_avatar.adapter as AvatarItemAdapter),
            AvatarItemAdapter.DetailsLookup(avatar_rv_avatar),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectSingleAnything()
        ).build()

        (avatar_rv_avatar.adapter as AvatarItemAdapter).selectionTracker = avatarTracker

        if (avatarViewModel.avatarWasSelected()) {
            avatarTracker?.select(avatarViewModel.selectedItem.value!!)
            avatar_rv_avatar.smoothScrollToPosition(avatarViewModel.selectedItem.value!!.toInt())
        }

        // We also need to observe selection changes in the RecyclerView.
        avatarTracker?.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                val iterator = avatarTracker?.selection?.iterator()
                if (iterator!!.hasNext()) {
                    val itemPressed = iterator.next()
                    avatarViewModel.setSelectedItem(itemPressed)
                }
            }
        })

        // Observe the changes in the list of the avatars and update the adapter
        avatarViewModel.avatarItems.observe(this, Observer<List<Avatar>> {
                it?.let {
                    avatarAdapter.avatarItems = it
                    avatarAdapter.notifyDataSetChanged()
                }
            })
    }

    fun generateNewUser() {
        Log.i(TAG, "Set the user")
    }

    /**
     * We need to save the instance state of the selectionTracker, otherwise
     * the selected item will be lost.
     * TODO: See if this is possible without the onSaveinstanceState but with an observer.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        avatarTracker!!.onSaveInstanceState(outState)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         */
        fun newInstance() =
            AvatarFragment()
    }
}
