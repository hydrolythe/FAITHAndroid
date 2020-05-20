package be.hogent.faith.faith.skyscraper

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentSkyscraperStartBinding
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.enterEventDetails.Skyscraper
import be.hogent.faith.faith.emotionCapture.enterEventDetails.SkyscraperAdapter
import org.koin.android.ext.android.getKoin


class SkyscraperStartScreenFragment : Fragment() {

    private var navigation: SkyscraperNavigationListener? = null
    private lateinit var binding: FragmentSkyscraperStartBinding
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()
    private lateinit var adapter: SkyscraperAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_skyscraper_start, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    private fun updateUI() {
        adapter = SkyscraperAdapter()
        binding.recyclerView2.layoutManager = GridLayoutManager(activity, 5)
        binding.recyclerView2.adapter = adapter
        val list = arrayListOf<Skyscraper>()
        list.add(Skyscraper("Dit is een test"))
        list.add(Skyscraper("Dit is een tweede wolkenkrabber"))
        adapter.submitList(list)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SkyscraperNavigationListener) {
            navigation = context
        }
    }

    companion object {
        fun newInstance(): SkyscraperStartScreenFragment {
            return SkyscraperStartScreenFragment()
        }
    }

    interface SkyscraperNavigationListener {

        fun goBack()
    }
}
