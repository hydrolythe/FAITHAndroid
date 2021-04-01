package be.hogent.faith.faith.skyscraper.startscreen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentSkyscraperOverviewBinding
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.models.goals.Goal
import be.hogent.faith.faith.skyscraper.SkyscraperActivity
import com.mikepenz.materialdrawer.util.ifNull
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class SkyscraperOverviewFragment : Fragment(), SkyscraperPanelTextListener {

    private var navigation: SkyscraperNavigationListener? = null
    private lateinit var binding: FragmentSkyscraperOverviewBinding

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()
    private val overviewViewModel: SkyscraperOverviewViewModel by viewModel {
        parametersOf(userViewModel.user.value)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_skyscraper_overview,
                container,
                false
            )
        binding.lifecycleOwner = this
        binding.viewModel = overviewViewModel

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        updateUI()
        startListeners()
    }

    private fun updateUI() {
        binding.skyscraperRv.layoutManager = GridLayoutManager(requireContext(), 5)

        binding.skyscraperRv.adapter.ifNull {
            binding.skyscraperRv.adapter =
                SkyscraperAdapter(requireNotNull(activity) as SkyscraperActivity, this)
        }
    }

    private fun startListeners() {
        binding.btnSkyscraperReturn.setOnClickListener {
            navigation?.closeSkyscrapers()
        }
        binding.btnSkyscraperHistory.setOnClickListener {
            navigation?.openSkyscrapersHistory()
        }
        overviewViewModel.goals.observe(viewLifecycleOwner, Observer { goals ->
            (binding.skyscraperRv.adapter as SkyscraperAdapter).submitList(goals.filter { !it.isCompleted })
        })
        overviewViewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessageResourceId ->
            Toast.makeText(requireContext(), errorMessageResourceId, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SkyscraperNavigationListener) {
            navigation = context
        }
    }

    companion object {
        fun newInstance(): SkyscraperOverviewFragment {
            return SkyscraperOverviewFragment()
        }
    }

    interface SkyscraperNavigationListener {
        fun closeSkyscrapers()
        fun openSkyscrapersHistory()
    }

    override fun onPanelTextChanged(goal: Goal, newText: String) {
        Timber.i("Paneltextchanged for goal ${goal.uuid}")
        overviewViewModel.updateGoalDescription(goal, newText)
    }
}
