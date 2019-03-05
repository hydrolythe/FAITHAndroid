package be.hogent.faith.faith.chooseAvatar.fragments

import android.os.Bundle
import android.util.Log
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
import be.hogent.faith.faith.util.TAG
import be.hogent.faith.faith.util.getRotation
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
    private var backPackTracker: SelectionTracker<Long>? = null

    override fun onStart() {
        super.onStart()
        registerAdapters()
        observeViewModel(avatar_rv_avatar)
        observeViewModel(avatar_rv_backpack)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_avatar, container, false)
    }

    /**
     * Registers the adapters for the RecyclerViews. Checks for the Orientation of the device and bases
     * the LayoutManager for the Adapter based on this.
     */
    private fun registerAdapters() {
        avatarViewModel = ViewModelProviders.of(this).get(AvatarItemViewModel::class.java)

        val orientation = (activity as AppCompatActivity).getRotation()
        when (orientation) {
            R.integer.PORTRAIT -> {
                avatar_rv_avatar.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                avatar_rv_avatar.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

                avatar_rv_backpack.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                avatar_rv_backpack.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
            R.integer.LANDSCAPE -> {
                avatar_rv_avatar.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
                avatar_rv_avatar.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))

                avatar_rv_backpack.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
                avatar_rv_backpack.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
            }
        }

        val avatarAdapter = AvatarItemAdapter(avatarViewModel, this, Glide.with(this))
        val backpackAdapter = AvatarItemAdapter(avatarViewModel, this, Glide.with(this))
        avatar_rv_avatar.adapter = avatarAdapter
        avatar_rv_backpack.adapter = backpackAdapter


        LinearSnapHelper().attachToRecyclerView(avatar_rv_avatar)
        LinearSnapHelper().attachToRecyclerView(avatar_rv_backpack)

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

        //We also need to observe selection changes in the RecyclerView.
        avatarTracker?.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                Log.i(TAG, "gedrukt op " + avatarTracker?.selection)
            }
        }


        )





        backPackTracker = SelectionTracker.Builder<Long>(
            "backpackSelection",
            avatar_rv_backpack,
            AvatarItemAdapter.KeyProvider(avatar_rv_backpack.adapter as AvatarItemAdapter),
            AvatarItemAdapter.DetailsLookup(avatar_rv_backpack),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
           SelectionPredicates.createSelectSingleAnything()
        ).build()

        (avatar_rv_backpack.adapter as AvatarItemAdapter).tracker = backPackTracker

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
