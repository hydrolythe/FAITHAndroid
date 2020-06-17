package be.hogent.faith.faith.skyscraper.goal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.SkyscraperActionRvItemBinding
import be.hogent.faith.domain.models.goals.Action

class ActionAdapter(private val actionListener: ActionListener) :
    ListAdapter<Action, ActionAdapter.ViewHolder>(ActionDiffCallback()),
    ActionTouchHelperCallback.IActionTouchHelper {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater
            .from(parent.context)
        val binding: SkyscraperActionRvItemBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.skyscraper_action_rv_item,
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    fun updateAction(position: Int, description: String) {
        if (getItem(position).description != description)
            actionListener.onActionUpdated(position, description)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        actionListener.onActionMove(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        actionListener.onActionDismiss(position)
    }

    inner class ViewHolder(private val itemBinding: SkyscraperActionRvItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(action: Action, position: Int) {
            if (action.description.isNotEmpty()) {
                itemBinding.txtActionDescription.tag = position
                itemBinding.txtActionDescription.setText(action.description)
            }
        }

        init {
            itemBinding.txtActionDescription.doOnTextChanged { text, start, count, after ->
                updateAction(itemBinding.txtActionDescription.tag.toString().toInt(), text.toString())
            }
        }
    }
}

interface ActionListener {
    fun onActionDismiss(position: Int)
    fun onActionMove(fromPosition: Int, toPosition: Int)
    fun onActionUpdated(position: Int, description: String)
}