package be.hogent.faith.faith.skyscraper.goal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.SkyscraperActionRvItemBinding
import be.hogent.faith.domain.models.goals.Action
import com.jakewharton.rxbinding4.widget.afterTextChangeEvents
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class ActionAdapter(private val actionListener: ActionListener) :
    ListAdapter<Action, ActionAdapter.ActionViewHolder>(ActionDiffCallback()),
    ActionTouchHelperCallback.IActionTouchHelper {

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

    inner class ActionViewHolder(
        private val view: SkyscraperActionRvItemBinding,
        private val actionListener: ActionListener
    ) :
        RecyclerView.ViewHolder(view.root) {
        private var disposables = CompositeDisposable()

        fun bind(action: Action, position: Int) {
            view.txtActionDescription.tag = position
            view.txtActionDescription.setText(action.description)
            disposables.clear()
            disposables.add(view.txtActionDescription.afterTextChangeEvents()
                .skip(1)
                .debounce(1, TimeUnit.SECONDS)
                .map {
                    actionListener.onActionUpdated(
                        view.txtActionDescription.tag.toString().toInt(), it.editable.toString()
                    )
                }
                .subscribe())
        }
    }
}

interface ActionListener {
    fun onActionDismiss(position: Int)
    fun onActionMove(fromPosition: Int, toPosition: Int)
    fun onActionUpdated(position: Int, description: String)
}