package be.hogent.faith.faith.skyscraper.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.goals.SubGoal
import kotlinx.android.synthetic.main.subgoal_rv_item.view.rv_subgoal_actions
import kotlinx.android.synthetic.main.subgoal_rv_item.view.subgoal_description

class SubgoalAdapter(
    val subgoals: Map<Int, SubGoal>
) : RecyclerView.Adapter<SubgoalAdapter.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.subgoal_rv_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position
        holder.subgoalDescription?.text = subgoals.get(position)?.description

        val childLayoutManager = LinearLayoutManager(holder.itemView.rv_subgoal_actions.context, RecyclerView.VERTICAL, false)

        holder.itemView.rv_subgoal_actions.apply {
            layoutManager = childLayoutManager
            adapter = SubgoalActionAdapter(subgoals.get(position)!!)
            setRecycledViewPool(viewPool)
        }
    }

    override fun getItemCount(): Int {
        return subgoals.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val subgoalDescription = view.subgoal_description
    }
}