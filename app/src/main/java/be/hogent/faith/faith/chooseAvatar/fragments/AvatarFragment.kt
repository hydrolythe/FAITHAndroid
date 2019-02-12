package be.hogent.faith.faith.chooseAvatar.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.faith.util.getRotation
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_avatar.*

private const val TAG = "AvatarFragment"

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
    private lateinit var avatarViewModel: AvatarViewModel
    /**
     * ViewModel used for the backpacks.
     */
    private lateinit var backpackViewModel: BackpackViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        avatarViewModel = ViewModelProviders.of(this).get(AvatarViewModel::class.java)
        backpackViewModel = ViewModelProviders.of(this).get(BackpackViewModel::class.java)

        val a = activity as AppCompatActivity
        val orientation = a.getRotation()
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
        val backpackAdapter = AvatarItemAdapter(backpackViewModel, this, Glide.with(this))
        avatar_rv_avatar.adapter = avatarAdapter
        avatar_rv_backpack.adapter = backpackAdapter
        obserViewModel(avatar_rv_avatar)
        obserViewModel(avatar_rv_backpack)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_avatar, container, false)
        return view
    }

    /**
     * Allows this fragment to observer the Recyclerview.
     */
    private fun obserViewModel(rv: RecyclerView) {
        avatarViewModel.avatarItems.observe(this, Observer {
            if (it != null) {
                rv.visibility = View.VISIBLE
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
