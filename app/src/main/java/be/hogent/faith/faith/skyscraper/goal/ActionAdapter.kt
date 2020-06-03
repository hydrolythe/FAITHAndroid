package be.hogent.faith.faith.skyscraper.goal

import be.hogent.faith.databinding.SkyscraperActionRvItemBinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R


class ActionAdapter(private val actionListener: ActionListener) :
    RecyclerView.Adapter<ActionAdapter.ViewHolder>() {

    private var _actions = emptyList<Action>().toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater
            .from(parent.context)
        val binding: SkyscraperActionRvItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.skyscraper_action_rv_item, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return _actions.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(_actions[position])
    }

    fun updateActionsList(newActions: List<Action>) {
        val diffCallback = ActionListDiffCallback(_actions, newActions)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        _actions.clear()
        _actions.addAll(newActions)
        diffResult.dispatchUpdatesTo(this)
    }
    fun removeItem(viewHolder: ViewHolder){
        _actions.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)
    }

    inner class ViewHolder(private val itemBinding: SkyscraperActionRvItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(action: Action){
            if(action.description.isNotEmpty())
            itemBinding.txtActionDescription.setText(action.description)

            itemBinding.txtActionDescription.setOnClickListener{
                actionListener.onActionClicked(action)
            }
        }
    }
}
data class Action(
    var description: String
)
interface ActionListener {
    fun onActionClicked(action: Action)

}