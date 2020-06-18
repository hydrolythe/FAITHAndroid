package be.hogent.faith.faith.skyscraper.goal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import io.reactivex.rxjava3.disposables.CompositeDisposable
import be.hogent.faith.databinding.SkyscraperSubgoalRvItemBinding
import be.hogent.faith.domain.models.goals.SubGoal

class SubGoalAdapter(
    private val onSubGoalSelectedListener: SubGoalSelectedListener,
    var floorHeight: Int
) :
    ListAdapter<SubGoal?, SubGoalAdapter.SubGoalViewHolder>(SugGoalDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubGoalViewHolder {
        val layoutInflater = LayoutInflater
            .from(parent.context)
        val binding: SkyscraperSubgoalRvItemBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.skyscraper_subgoal_rv_item,
                parent,
                false
            )
        return SubGoalViewHolder(binding, onSubGoalSelectedListener, floorHeight)
    }

    override fun onBindViewHolder(holder: SubGoalViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class SubGoalViewHolder(
        private val view: SkyscraperSubgoalRvItemBinding,
        private val subGoalSelectedListener: SubGoalSelectedListener,
        floorHeight: Int
    ) :
        RecyclerView.ViewHolder(view.root) {
        private var disposables = CompositeDisposable()

        init {
            view.txtSubgoalDescription.layoutParams.height = floorHeight
            view.txtSubgoalDescription.setOnClickListener {
                subGoalSelectedListener.onSubGoalSelected(
                    view.txtSubgoalDescription.tag.toString().toInt()
                )
            }
        }

        fun bind(subGoal: SubGoal?, position: Int) {
            view.txtSubgoalDescription.tag = position
            view.txtSubgoalDescription.setText(subGoal?.description)
        }
    }
}

class SugGoalDiffCallback : DiffUtil.ItemCallback<SubGoal?>() {
    override fun areItemsTheSame(oldItem: SubGoal, newItem: SubGoal): Boolean {
        return oldItem.description == newItem.description
    }

    override fun areContentsTheSame(oldItem: SubGoal, newItem: SubGoal): Boolean {
        return oldItem == newItem
    }
}

interface SubGoalSelectedListener {
    fun onSubGoalSelected(position: Int)
}