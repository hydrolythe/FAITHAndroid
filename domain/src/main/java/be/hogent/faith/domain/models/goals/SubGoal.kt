package be.hogent.faith.domain.models.goals

class SubGoal(
    var description: String
) {

    var currentPosition: Int = 0

    private val _actions = mutableListOf<Action>()
    val actions: List<Action>
        get() = _actions

    fun addActionToSubGoal(newAction: Action) {
        _actions.add(newAction)
    }

    fun removeSubGoalFromGoal(action: Action) {
        if (_actions.contains(action))
            _actions.remove(action)
        else
            throw IllegalArgumentException("Unable to find the selected Action")
    }

    fun updateActionPosition(actionToUpdate: Action, newPosition: Int) {
        _actions.remove(actionToUpdate)
        _actions.add(newPosition, actionToUpdate)
        updateActionPositions()
    }

    private fun updateActionPositions() {
        for (action in _actions)
            action.changeCurrentPosition(_actions.indexOf(action))
    }

    fun editDescription(newDescription: String) {
        if (newDescription.length <= 30)
            description = newDescription
        else
            throw IllegalArgumentException("Description > 30 characters") }

    fun changeCurrentPosition(newPosition: Int) { currentPosition = newPosition }
}