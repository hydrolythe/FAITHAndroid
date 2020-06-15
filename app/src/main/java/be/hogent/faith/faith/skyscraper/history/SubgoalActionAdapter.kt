package be.hogent.faith.faith.skyscraper.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.goals.SubGoal
import kotlinx.android.synthetic.main.subgoal_action_rv_item.view.action_description


class SubgoalActionAdapter(
    val subgoal: SubGoal

) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return subgoal.actions.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.actionDescription?.text = subgoal.actions.get(position).description
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.subgoal_action_rv_item, parent, false))
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val actionDescription = view.action_description
}
