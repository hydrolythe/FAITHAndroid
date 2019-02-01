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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.faith.util.getRotation
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_avatar.*


/**
 * A [Fragment] subclass which allows the user to choose an Avatar and a Backpack.
 *
 * Use the [AvatarFramgent.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AvatarFramgent : Fragment() {


    private lateinit var viewModel: ListViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)

        val a = activity as AppCompatActivity
        val orientation = a.getRotation()
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
        val adapter = AvatarAdapter(viewModel, this, Glide.with(this))
        avatar_rv_avatar.adapter = adapter
        obserViewModel(avatar_rv_avatar)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_avatar, container, false)

        return view
    }

    private fun obserViewModel(rv: RecyclerView) {
        viewModel.avatars.observe(this, Observer {
            if (it != null) {
                rv.visibility = View.VISIBLE
                Log.i("TAG", "gelukt")
            }
        })
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         */
        @JvmStatic
        fun newInstance() =
            AvatarFramgent()

        private val TAG = this::class.java.simpleName

    }
}
