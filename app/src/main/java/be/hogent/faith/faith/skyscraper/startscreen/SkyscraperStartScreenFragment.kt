package be.hogent.faith.faith.skyscraper.startscreen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentSkyscraperStartBinding
import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.domain.models.goals.GoalColor
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.skyscraper.SkyscraperActivity
import kotlinx.android.synthetic.main.skyscraper_rv_blue.view.txt_goal_description
import org.koin.android.ext.android.getKoin

class SkyscraperStartScreenFragment : Fragment(),
    SkyscraperClickListener {

    private var navigation: SkyscraperNavigationListener? = null
    private lateinit var binding: FragmentSkyscraperStartBinding
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()
    private lateinit var adapter: SkyscraperAdapter
    val list = arrayListOf<Goal>()
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
        setOnclickListeners()
    }

    private fun updateUI() {
        adapter = SkyscraperAdapter(
            requireNotNull(activity) as SkyscraperActivity,
            this
        )
        binding.skyscraperRv.layoutManager = GridLayoutManager(requireContext(), 5)
        binding.skyscraperRv.adapter = adapter
        adapter.submitList(list)
    }

    private fun setOnclickListeners() {
        binding.btnSkyscraperReturn.setOnClickListener {
            navigation?.closeSkyscrapers()
        }
        binding.btnSkyscraperHistory.setOnClickListener {
            navigation?.openSkyscrapersHistory()
        }
        binding.btnSkyscraperAdd.setOnClickListener {
            list.add(Goal(GoalColor.GREEN))
            adapter.submitList(list)
            adapter.notifyDataSetChanged()
        }
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
        fun closeSkyscrapers()
        fun openSkyscrapersHistory()
    }

    override fun getSelectedSkyscraper(layout: ConstraintLayout, position: Int) {
        val help = layout.txt_goal_description.text
        Toast.makeText(requireContext(), "$help", Toast.LENGTH_LONG).show()
    }
}
