package be.hogent.faith.faith.skyscraper.goal

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.SkyscraperActionRvItemBinding
import be.hogent.faith.domain.models.goals.Action
import be.hogent.faith.domain.models.goals.ActionStatus

class ActionAdapter(private val actionListener: ActionListener) :
    RecyclerView.Adapter<ActionAdapter.ActionViewHolder>(),
    ItemTouchHelperCallback.IItemTouchHelper {

    private var actionsList = ArrayList<Action>()

    var selectedSubgoalIndex: Int? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionViewHolder {
        val layoutInflater = LayoutInflater
            .from(parent.context)
        val binding: SkyscraperActionRvItemBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.skyscraper_action_rv_item,
                parent,
                false
            )
        return ActionViewHolder(binding, actionListener)
    }

    override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
        holder.bind(actionsList[position], position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        actionListener.onActionMove(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        actionListener.onActionDismiss(position)
    }

    override fun getItemCount(): Int {
        return actionsList.size
    }

    fun setData(list: List<Action>?) {
        list?.let {
            actionsList.clear()
            actionsList.addAll(list)
            notifyDataSetChanged()
        }
    }

    inner class ActionViewHolder(
        private val binding: SkyscraperActionRvItemBinding,
        private val actionListener: ActionListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(action: Action, position: Int) {
            // the tag contains the key of the goal and the index of the action
            binding.txtActionDescription.tag = "${selectedSubgoalIndex}$position"
            binding.txtActionDescription.setText(action.description)
            val background = binding.txtActionDescription.background as GradientDrawable
            background.setColor(
                when (action.currentStatus) {
                    ActionStatus.ACTIVE -> ContextCompat.getColor(
                        binding.txtActionDescription.context,
                        R.color.skyscraper_action_active
                    )
                    ActionStatus.NEUTRAL -> ContextCompat.getColor(
                        binding.txtActionDescription.context,
                        R.color.color_white
                    )
                    ActionStatus.NON_ACTIVE -> ContextCompat.getColor(
                        binding.txtActionDescription.context,
                        R.color.skyscraper_action_inactive
                    )
                }
            )
            if (action.description.isEmpty()) binding.txtActionDescription.requestFocus()
            binding.swap.setOnClickListener {
                actionListener.onActionUpdateState(
                    binding.txtActionDescription.tag.toString().toInt()
                )
            }

            binding.txtActionDescription.setOnFocusChangeListener { _, _ ->
                actionListener.onActionUpdated(
                    binding.txtActionDescription.tag.toString().toInt(),
                    binding.txtActionDescription.text.toString()
                )
            }
        }
    }
}

interface ActionListener {
    fun onActionDismiss(position: Int)
    fun onActionMove(fromPosition: Int, toPosition: Int)
    fun onActionUpdated(position: Int, description: String)
    fun onActionUpdateState(position: Int)
}