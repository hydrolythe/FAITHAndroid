package be.hogent.faith.faith.skyscraper.goal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.SkyscraperSubgoalRvItemBinding
import be.hogent.faith.domain.models.goals.GoalColor
import be.hogent.faith.domain.models.goals.SubGoal

class SubGoalAdapter(
    private val goalColor: GoalColor,
    private val onSubGoalSelectedListener: SubGoalListener,
    var floorHeight: Int
) :
    RecyclerView.Adapter<SubGoalAdapter.SubGoalViewHolder>(),
    ItemTouchHelperCallback.IItemTouchHelper {

    private var subGoalsList = ArrayList<SubGoal>();

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
        return SubGoalViewHolder(binding, onSubGoalSelectedListener, goalColor, floorHeight)
    }

    override fun onBindViewHolder(holder: SubGoalViewHolder, position: Int) {
        holder.bind(subGoalsList[position], position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        onSubGoalSelectedListener.onSubGoalMove(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        onSubGoalSelectedListener.onSubGoalDismiss(position)
    }

    inner class SubGoalViewHolder(
        private val view: SkyscraperSubgoalRvItemBinding,
        private val subGoalSelectedListener: SubGoalListener,
        goalColor: GoalColor,
        floorHeight: Int
    ) : RecyclerView.ViewHolder(view.root) {

        init {
            with(view.txtSubgoalDescription) {
                layoutParams.height = floorHeight

                setBackgroundResource(
                    when (goalColor) {
                        GoalColor.BLUE -> R.color.skyscraper_blue
                        GoalColor.YELLOW -> R.color.skyscraper_yellow
                        GoalColor.RED -> R.color.skyscraper_red
                        GoalColor.DARKGREEN -> R.color.skyscraper_darkgreen
                        GoalColor.GREEN -> R.color.skyscraper_green
                        else -> R.color.skyscraper_blue
                    }
                )

                setTextColor(
                    if (goalColor == GoalColor.YELLOW)
                        ContextCompat.getColor(view.txtSubgoalDescription.context, R.color.black)
                    else
                        ContextCompat.getColor(
                            view.txtSubgoalDescription.context,
                            R.color.color_white
                        )
                )
            }
        }

        fun bind(subGoal: SubGoal?, position: Int) {
            with(view.txtSubgoalDescription) {
                tag = position
                text = subGoal?.description
            }
            view.txtSubgoalDescription.setOnClickListener {
                it.isFocusable = true
                it.isFocusableInTouchMode = true
                it.requestFocus()
                subGoalSelectedListener.onSubGoalSelected(
                    position
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return subGoalsList.size
    }

    fun setData(list: List<SubGoal>){
        subGoalsList.clear()
        subGoalsList.addAll(list)
        notifyDataSetChanged()
    }
}


interface SubGoalListener {
    fun onSubGoalDismiss(position: Int)
    fun onSubGoalMove(fromPosition: Int, toPosition: Int)
    fun onSubGoalSelected(position: Int)
}