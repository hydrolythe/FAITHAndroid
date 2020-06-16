package be.hogent.faith.faith.skyscraper.history

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import be.hogent.faith.R
import be.hogent.faith.databinding.DialogGoalOverviewBinding
import be.hogent.faith.domain.models.goals.Goal

class OverviewGoalDialog(private var goal: Goal) : DialogFragment() {
    private lateinit var binding: DialogGoalOverviewBinding
    private lateinit var adapter: SubgoalAdapter

    companion object {
        fun newInstance(goal: Goal): OverviewGoalDialog {
            return OverviewGoalDialog(goal)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireActivity(), R.style.Dialog_NearlyFullScreen).apply {
            setStyle(STYLE_NO_TITLE, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_goal_overview, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adapter = SubgoalAdapter(goal.subGoals)
        binding.rvSubgoals.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSubgoals.adapter = adapter
        binding.btnCloseOverview.setOnClickListener {
            dismiss()
        }
    }
}