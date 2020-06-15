package be.hogent.faith.faith.skyscraper.history

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentSkyscraperHistoryBinding
import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.domain.models.goals.SubGoal
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.skyscraper.SkyscraperActivity
import be.hogent.faith.faith.skyscraper.goal.Action
import be.hogent.faith.faith.skyscraper.startscreen.SkyscraperColors
import org.koin.android.ext.android.getKoin

class SkyscraperHistoryFragment : Fragment() {

    private var navigation: SkyscraperNavigationListener? = null
    private lateinit var binding: FragmentSkyscraperHistoryBinding
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()
    private lateinit var adapter: HistoryAdapter
    private lateinit var overviewGoalDialog: OverviewGoalDialog
    val list = arrayListOf<Goal>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_skyscraper_history, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        updateUI()
        setUpRecyclerView()
        setOnclickListeners()
    }

    private fun setUpRecyclerView() {
        adapter = HistoryAdapter(
            requireNotNull(activity) as SkyscraperActivity
        )
        binding.recyclerView.layoutManager = GridLayoutManager(activity, 5)
        binding.recyclerView.adapter = adapter

        /*
        val helpList = arrayListOf<SubGoal>()
            val helpList2 = arrayListOf<Action>()
            helpList2.add(Action("Dit is een actie"))
            helpList2.add(Action("Dit is een andere actie"))
            helpList.add(SubGoal("Dit is een subgoal"))
            helpList.add(SubGoal("Dit is een andere subgoal",helpList2))

        list.add(Goal("Dit is een eerste goal",SkyscraperColors.SKYSCRAPER_BLUE, helpList))
        list.add(Goal("Dit is een tweede goal",SkyscraperColors.SKYSCRAPER_DARK_GREEN, helpList))
        list.add(Goal("Dit is een derde goal",SkyscraperColors.SKYSCRAPER_GREEN, helpList))
        list.add(Goal("Dit is een vierde goal",SkyscraperColors.SKYSCRAPER_YELLOW, helpList))
        list.add(Goal("Dit is een vierde actie",SkyscraperColors.SKYSCRAPER_YELLOW, helpList))

         */

        adapter.submitList(list)

    }

    private fun updateUI() {
    }
    private fun setOnclickListeners() {
        binding.btnSkyscraperReturn.setOnClickListener {
            navigation?.goBack()
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SkyscraperNavigationListener) {
            navigation = context
        }
    }

    /*private fun showOverviewDialog(goal: Goal) {
        overviewGoalDialog = OverviewGoalDialog.newInstance(goal)
        overviewGoalDialog.show(requireActivity().supportFragmentManager, null)
    }*/

    companion object {
        fun newInstance(): SkyscraperHistoryFragment {
            return SkyscraperHistoryFragment()
        }
    }

    interface SkyscraperNavigationListener {
        fun goBack()
    }
}
