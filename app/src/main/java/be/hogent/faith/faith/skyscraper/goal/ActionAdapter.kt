package be.hogent.faith.faith.skyscraper.goal

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.SkyscraperActionRvItemBinding
import be.hogent.faith.domain.models.goals.Action
import be.hogent.faith.domain.models.goals.ActionStatus

class ActionAdapter(private val actionListener: ActionListener) :
    ListAdapter<Action, ActionAdapter.ActionViewHolder>(ActionDiffCallback()),
    ItemTouchHelperCallback.IItemTouchHelper {

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
        holder.bind(getItem(position), position)
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

        fun bind(action: Action, position: Int) {
            // the tag contains the key of the goal and the index of the action
            view.txtActionDescription.tag = "${selectedSubgoalIndex}$position"
            view.txtActionDescription.setText(action.description)
            val background = view.txtActionDescription.background as GradientDrawable
            background.setColor(
                when (action.currentStatus) {
                    ActionStatus.ACTIVE -> ContextCompat.getColor(
                        view.txtActionDescription.context,
                        R.color.skyscraper_action_active
                    )
                    ActionStatus.NEUTRAL -> ContextCompat.getColor(
                        view.txtActionDescription.context,
                        R.color.color_white
                    )
                    ActionStatus.NON_ACTIVE -> ContextCompat.getColor(
                        view.txtActionDescription.context,
                        R.color.skyscraper_action_inactive
                    )
                }
            )
            if (action.description.isEmpty()) view.txtActionDescription.requestFocus()
            view.swap.setOnClickListener(object : View.OnClickListener {
                override fun onClick(button: View?) {
                    actionListener.onActionUpdateState(
                        view.txtActionDescription.tag.toString().toInt()
                    )
                }
            })

            view.txtActionDescription.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    actionListener.onActionUpdated(
                        view.txtActionDescription.tag.toString().toInt(), view.txtActionDescription.text.toString()
                    )
                }
            }

            /*
            view.txtActionDescription.afterTextChangeEvents()
                .skip(1)
                .debounce(1, TimeUnit.SECONDS)
                .map {
                    Timber.i("editable ${it.editable.toString()}")
                    actionListener.onActionUpdated(
                        view.txtActionDescription.tag.toString().toInt(), it.editable.toString()
                    )
                }
                .subscribe()
*/
        }
    }
}

interface ActionListener {
    fun onActionDismiss(position: Int)
    fun onActionMove(fromPosition: Int, toPosition: Int)
    fun onActionUpdated(position: Int, description: String)
    fun onActionUpdateState(position: Int)
}