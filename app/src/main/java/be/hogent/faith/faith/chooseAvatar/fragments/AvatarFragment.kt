package be.hogent.faith.faith.chooseAvatar.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import be.hogent.faith.R
import be.hogent.faith.faith.util.getRotation
import com.bumptech.glide.Glide
import org.koin.android.viewmodel.ext.android.viewModel


/**
 * A [Fragment] subclass which allows the user to choose an Avatar and a Backpack.
 *
 * Use the [AvatarFramgent.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AvatarFramgent : Fragment() {


    private lateinit var viewModel: ListViewModel




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_avatar, container, false)
        val rv = view.findViewById(R.id.avatar_rv_avatar) as RecyclerView
        rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        val a = activity as AppCompatActivity
        val orientation = a.getRotation()
        when (orientation) {
            R.integer.PORTRAIT -> rv.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            R.integer.LANDSCAPE -> rv.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        }
        val adapter = AvatarAdapter(viewModel, this, Glide.with(this))
        rv.adapter = adapter
        obserViewModel(rv)
        return view;
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
