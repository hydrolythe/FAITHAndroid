package be.hogent.faith.faith.models.goals

import java.io.Serializable
import java.util.Collections

private const val DESCRIPTION_MAX_LENGTH = 30

data class SubGoal(
    var description: String
) : Serializable {

    private val _actions = mutableListOf<Action>()
    val actions: List<Action>
        get() = _actions

    fun addAction(newAction: Action) {
        _actions.add(newAction)
    }

    fun removeAction(action: Action) {
        require(_actions.contains(action)) { "Deze actie hoort niet bij dit subdoel" }
        _actions.remove(action)
    }

    fun removeAction(position: Int) {
        if (position < _actions.size)
            _actions.removeAt(position)
    }

    fun updateAction(position: Int, description: String) {
        _actions[position].description = description
    }

    fun updateActionStatus(position: Int) {
        _actions[position].currentStatus = when (_actions[position].currentStatus) {
            ActionStatus.ACTIVE -> ActionStatus.NON_ACTIVE
            ActionStatus.NON_ACTIVE -> ActionStatus.NEUTRAL
            ActionStatus.NEUTRAL -> ActionStatus.ACTIVE
        }
    }

    fun updateActionPosition(actionToUpdate: Action, newPosition: Int) {
        _actions.remove(actionToUpdate)
        _actions.add(newPosition, actionToUpdate)
    }

    fun updateActionPosition(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(_actions, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(_actions, i, i - 1)
            }
        }
    }

    fun copy(): SubGoal {
        val subgoal = SubGoal(this.description)
        actions.forEach { subgoal.addAction(it.copy()) }
        return subgoal
    }
}