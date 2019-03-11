package be.hogent.faith.faith.chooseAvatar.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.faith.util.getRotation
import be.hogent.faith.faith.util.getViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_avatar.*

/**
 * A [Fragment] subclass which allows the user to choose an AvatarItem and a Backpack.
 *
 * Use the [AvatarFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AvatarFragment : Fragment() {

    /**
     * ViewModel used for the avatarItems.
     */
    private lateinit var avatarViewModel: AvatarItemViewModel

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
        observeViewModel(avatar_rv_avatar)
        avatar_btn_go_to_town.setOnClickListener{
            avatarViewModel.nextButtonPressed()

        }
    }

    override fun onPause() {
        super.onPause()

        avatar_btn_go_to_town.setOnClickListener(null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_avatar, container, false)
    }

    /**
     * Changes the orientation of the recyclerviews, depending on the orientation of the device;
     */
    private fun setOrientation() {
        val orientation = (activity as AppCompatActivity).getRotation()
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
     * Registers the adapters for the RecyclerViews. Checks for the orientation of the device and sets
     * the LayoutManager for the Adapter based on this.
     */
    private fun registerAdapters() {
        avatarViewModel = getViewModel(AvatarItemViewModel::class.java)
        //avatarViewModel = ViewModelProviders.of(this).get(AvatarItemViewModel::class.java)

            val avatarAdapter = AvatarItemAdapter(avatarViewModel, this, Glide.with(this))
            avatar_rv_avatar.adapter = avatarAdapter

            LinearSnapHelper().attachToRecyclerView(avatar_rv_avatar)

        setOrientation()

            avatarTracker = SelectionTracker.Builder<Long>(
                "avatarSelection",
                avatar_rv_avatar,
                AvatarItemAdapter.KeyProvider(avatar_rv_avatar.adapter as AvatarItemAdapter),
                AvatarItemAdapter.DetailsLookup(avatar_rv_avatar),
                StorageStrategy.createLongStorage()
            ).withSelectionPredicate(
                SelectionPredicates.createSelectSingleAnything()
            ).build()

            (avatar_rv_avatar.adapter as AvatarItemAdapter).tracker = avatarTracker
            if (avatarViewModel.isSelected()) {
                avatarTracker?.select(avatarViewModel.selectedItem.value!!)
                avatar_rv_avatar.smoothScrollToPosition(avatarViewModel.selectedItem.value!!.toInt())
                avatarAdapter.notifyDataSetChanged()
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
    }

    /**
     * Allows this fragment to observe the item included in the adapter and sets the Visibility of the
     * RecyclerView to [View.VISIBLE] if this was not already the case.
     */
    private fun observeViewModel(recyclerView: RecyclerView) {
        avatarViewModel.avatarItems.observe(this, Observer {
            if (it != null) {
                recyclerView.visibility = View.VISIBLE
            }
        })
    }

    /**
     * We need to save the instance state of the tracker, otherwise
     * the selected item will be lost.
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
